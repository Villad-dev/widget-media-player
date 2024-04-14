package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import com.example.widgets.repos.Playlist

class MediaWidget : AppWidgetProvider() {

    companion object {
        private lateinit var mediaPlayer: MediaPlayer
    }
    private val playlist = Playlist()

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        println("onAppWidgetOptionsChanged")
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        if (newOptions != null) {
            val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

            val remoteViews = when {
                minHeight <= 57f -> buildView(context!!, playlist, R.layout.small_media)
                minHeight <= 126f -> buildView(context!!, playlist, R.layout.medium_media)
                else -> buildView(context!!, playlist, R.layout.large_media)
            }

            appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        println("onUpdate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            updateWidgetComponents(context, null)
            mediaPlayer = MediaPlayer.create(context, playlist.getCurrentSong().musicID).also {
                it.setOnCompletionListener {
                    val intentOnComplete = Intent(context, MediaWidget::class.java)
                    intentOnComplete.action = "RIGHT_ACTION"
                    context.sendBroadcast(intentOnComplete)
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        println("onReceive")

        when (intent.action) {
            "URL_ACTION" -> {
                openChromeWithUrl(context, playlist.getCurrentSong().URL)
            }

            "LEFT_ACTION" -> {
                playlist.previousSong()
                mediaPlayer = MediaPlayer.create(context, playlist.getCurrentSong().musicID).also {
                    it.setOnCompletionListener {
                        val intentOnComplete = Intent(context, MediaWidget::class.java)
                        intentOnComplete.action = "RIGHT_ACTION"
                        context.sendBroadcast(intentOnComplete)
                    }
                }
                updateWidgetComponents(context, R.drawable.play)
            }

            "RIGHT_ACTION" -> {
                playlist.nextSong()
                mediaPlayer = MediaPlayer.create(context, playlist.getCurrentSong().musicID).also {
                    it.setOnCompletionListener {
                        val intentOnComplete = Intent(context, MediaWidget::class.java)
                        intentOnComplete.action = "RIGHT_ACTION"
                        context.sendBroadcast(intentOnComplete)
                    }
                }
                updateWidgetComponents(context, R.drawable.play)
            }

            "RESET_ACTION" -> {
                updateWidgetComponents(context, R.drawable.play)
                mediaPlayer.stop()
                mediaPlayer.prepare()
            }

            "PLAY_STOP_ACTION" -> {
                if (mediaPlayer.isPlaying) {
                    println("PLAYING")
                    mediaPlayer.pause()
                    updateWidgetComponents(context, R.drawable.play)
                } else {
                    println("NOT PLAYING")
                    mediaPlayer.start()
                    updateWidgetComponents(context, R.drawable.pause_circle)
                }
            }
        }
    }

    private fun buildView(context: Context, playlist: Playlist, mediaLayout: Int): RemoteViews {
        val views = RemoteViews(context.packageName, mediaLayout)
        val song = playlist.getCurrentSong()

        views.setTextViewText(R.id.artist_name, song.artistName)
        views.setTextViewText(R.id.song_name, song.songName)
        views.setImageViewResource(R.id.imageView, song.imageID)

        elementSetOnClickListener(context, "URL_ACTION", mediaLayout, views, R.id.imageView)
        elementSetOnClickListener(context, "LEFT_ACTION", mediaLayout, views, R.id.leftButton)
        elementSetOnClickListener(context, "RIGHT_ACTION", mediaLayout, views, R.id.rightButton)
        elementSetOnClickListener(
            context,
            "PLAY_STOP_ACTION",
            mediaLayout,
            views,
            R.id.stopPlayButton
        )
        elementSetOnClickListener(context, "RESET_ACTION", mediaLayout, views, R.id.resetButton)

        return views
    }

    private fun updateWidgetComponents(context: Context, imagePlayStop: Int?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, MediaWidget::class.java))

        widgetIds.forEach { widgetId ->
            val widgetOptions = appWidgetManager.getAppWidgetOptions(widgetId)
            val minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

            val remoteViews = when {
                minHeight <= 57f -> buildView(context, playlist, R.layout.small_media)
                minHeight <= 126f -> buildView(context, playlist, R.layout.medium_media)
                else -> buildView(context, playlist, R.layout.large_media)
            }
            if (imagePlayStop != null)
                remoteViews.setImageViewResource(R.id.stopPlayButton, imagePlayStop)

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }

    private fun elementSetOnClickListener(
        context: Context, actionString: String, requestCode: Int, views: RemoteViews, elementID: Int
    ) {
        val actionOnCLickIntent = Intent(context, MediaWidget::class.java)
        actionOnCLickIntent.action = actionString
        val actionOnClickPendingIntent = PendingIntent.getBroadcast(
            context, requestCode, actionOnCLickIntent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(elementID, actionOnClickPendingIntent)
    }


    private fun openChromeWithUrl(context: Context, url: String?) {
        url.let {
            try {
                val youtubePackageName = "com.google.android.youtube"
                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                youtubeIntent.setPackage(youtubePackageName)
                youtubeIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                context.startActivity(youtubeIntent)
            } catch (e: Exception) {
                Log.e("MediaWidget", "Error opening YouTube URL: ${e.message}")
            }
        }
    }

}