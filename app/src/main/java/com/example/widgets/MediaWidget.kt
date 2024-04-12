package com.example.widgets

import MyMediaPlayer
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SizeF
import android.widget.RemoteViews
import com.example.widgets.repos.Playlist


class MediaWidget : AppWidgetProvider() {

    private var playlist: Playlist = Playlist()
    private lateinit var mediaPlayer: MyMediaPlayer
    private lateinit var myTimer: MyTimer

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val smallView = buildView(context, playlist, R.layout.small_media)
            val mediumView = buildView(context, playlist, R.layout.medium_media)
            val largeView = buildView(context, playlist, R.layout.large_media)

            val viewMapping: Map<SizeF, RemoteViews> = mapOf(
                SizeF(349f, 102f) to smallView,
                SizeF(349f, 220f) to mediumView,
                SizeF(349f, 300f) to largeView
            )

            val multipleRemoteViews = RemoteViews(viewMapping)
            appWidgetIds.forEach { appWidgetId ->
                appWidgetManager.updateAppWidget(appWidgetId, multipleRemoteViews)
            }
        } else {
            val singleView = RemoteViews(context.packageName, R.layout.medium_media)
            appWidgetIds.forEach { widgetId ->
                updateAppWidget(context, appWidgetManager, widgetId, singleView)
            }
        }
    }

    private fun buildView(context: Context, playlist: Playlist, MediaLayout: Int): RemoteViews {
        val views = RemoteViews(context.packageName, MediaLayout)
        val song = playlist.getCurrentSong()

        views.setTextViewText(androidx.core.R.id.text, context.getString(R.string.appwidget_text))
        views.setTextViewText(R.id.artist_name, song.artistName)
        views.setTextViewText(R.id.song_name, song.songName)

        elementSetOnClickListener(context, "URL_ACTION", MediaLayout, views, R.id.imageView)
        elementSetOnClickListener(context, "LEFT_ACTION", MediaLayout, views, R.id.leftButton)
        elementSetOnClickListener(context, "RIGHT_ACTION", MediaLayout, views, R.id.rightButton)
        elementSetOnClickListener(
            context, "PLAY_STOP_ACTION", MediaLayout, views, R.id.stopPlayButton
        )
        elementSetOnClickListener(context, "RESET_ACTION", MediaLayout, views, R.id.resetButton)
        return views
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            "URL_ACTION" -> {
                openChromeWithUrl(context, playlist.getCurrentSong().URL)
            }

            "LEFT_ACTION" -> {
                playlist = Playlist()
                playlist.previousSong()
                updateWidgetComponents(context)
            }

            "RIGHT_ACTION" -> {
                playlist = Playlist()
                playlist.nextSong()
                updateWidgetComponents(context)
            }

            "RESET_ACTION" -> {
            }

            "PLAY_STOP_ACTION" -> {
            }
        }
    }

    private fun updateWidgetComponents(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, MediaWidget::class.java))

        widgetIds.forEach { widgetId ->
            val widgetOptions = appWidgetManager.getAppWidgetOptions(widgetId)
            val minHeight = widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

            val remoteViews = when {
                minHeight <= 57f -> buildView(context, playlist, R.layout.small_media)
                minHeight <= 126f -> buildView(context, playlist, R.layout.medium_media)
                else -> buildView(context, playlist, R.layout.large_media)
            }

            remoteViews.setTextViewText(androidx.core.R.id.text, context.getString(R.string.appwidget_text))
            remoteViews.setTextViewText(R.id.artist_name, playlist.getCurrentSong().artistName)
            remoteViews.setTextViewText(R.id.song_name, playlist.getCurrentSong().songName)

            appWidgetManager.updateAppWidget(widgetId, remoteViews)
        }
    }


    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        remoteViews: RemoteViews
    ) {
        remoteViews.setTextViewText(androidx.core.R.id.text, context.getString(R.string.appwidget_text))
        remoteViews.setTextViewText(R.id.artist_name, playlist.getCurrentSong().artistName)
        remoteViews.setTextViewText(R.id.song_name, playlist.getCurrentSong().songName)

        elementSetOnClickListener(context, "URL_ACTION", appWidgetId, remoteViews, R.id.imageView)
        elementSetOnClickListener(context, "LEFT_ACTION", appWidgetId, remoteViews, R.id.leftButton)
        elementSetOnClickListener(context, "RIGHT_ACTION", appWidgetId, remoteViews, R.id.rightButton)
        elementSetOnClickListener(
            context, "PLAY_STOP_ACTION", appWidgetId, remoteViews, R.id.stopPlayButton
        )
        elementSetOnClickListener(context, "RESET_ACTION", appWidgetId, remoteViews, R.id.resetButton)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
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
