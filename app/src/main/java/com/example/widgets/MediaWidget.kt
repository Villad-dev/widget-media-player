package com.example.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.widget.ProgressBar
import android.widget.RemoteViews

class MediaWidget : AppWidgetProvider() {

    companion object {
        private var currentIndex = 0
        private lateinit var mediaPlayer: MediaPlayer
        private lateinit var myTimer: MyTimer

        fun getCurrentIndex(): Int {
            return currentIndex
        }

        fun decrementIndex() {
            currentIndex = (currentIndex - 1 + listMusic.size) % listMusic.size
        }

        fun incrementIndex() {
            currentIndex = (currentIndex + 1) % listMusic.size
        }

        private val listMusic = listOf(
            Music(
                "Pink Floyd",
                "Time",
                R.raw.pink_floyd_time,
                R.drawable.pink_floyd,
                "https://www.youtube.com/watch?v=Qr0-7Ds79zo&ab_channel=PinkFloyd"
            ), Music(
                "Nirvana",
                "The Man Who Sold The World",
                R.raw.nirvana_the_man_who_solds_the_world,
                R.drawable.nirvana_album,
                "https://www.youtube.com/watch?v=YYfSbR8xs2c&ab_channel=Nirvana-Topic"
            ), Music(
                "The XX",
                "Intro",
                R.raw.the_xx,
                R.drawable.xx_album,
                "https://www.youtube.com/watch?v=xMV6l2y67rk&ab_channel=Thexx-Topic"
            )
        )
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(
            "Message",
            "$currentIndex Broadcast Received!!! ${intent.action} ${listMusic[currentIndex]}"
        )

        when (intent.action) {
            "URL_ACTION" -> {
                Log.i("Message", "URL_ACTION received $currentIndex")
                openChromeWithUrl(context, listMusic[currentIndex].URL)
            }

            "LEFT_ACTION" -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                Log.i("Message", "LEFT_ACTION received $currentIndex")
                decrementIndex()

                mediaPlayer = MediaPlayer.create(context, listMusic[currentIndex].musicID).also {
                    it.setOnCompletionListener {
                        val intentOnComplete = Intent(context, MediaWidget::class.java)
                        intentOnComplete.action = "RIGHT_ACTION"
                        context.sendBroadcast(intentOnComplete)
                    }
                }
                updateWidgetImage(context, R.drawable.play, R.id.stopPlayButton)
                updateWidgetImage(context, listMusic[currentIndex].imageID, R.id.imageView)
            }

            "RIGHT_ACTION" -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                Log.i("Message", "RIGHT_ACTION received $currentIndex")
                incrementIndex()

                mediaPlayer =
                    MediaPlayer.create(context, listMusic[getCurrentIndex()].musicID).also {
                        it.setOnCompletionListener {
                            val intentOnComplete = Intent(context, MediaWidget::class.java)
                            intentOnComplete.action = "RIGHT_ACTION"
                            context.sendBroadcast(intentOnComplete)
                        }
                    }
                updateWidgetImage(context, R.drawable.play, R.id.stopPlayButton)
                updateWidgetImage(context, listMusic[currentIndex].imageID, R.id.imageView)
            }

            "RESET_ACTION" -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                    myTimer.resetTimer()
                    updateWidgetImage(context, R.drawable.play, R.id.stopPlayButton)
                }
            }

            "PLAY_STOP_ACTION" -> {
                if (mediaPlayer.isPlaying) {
                    println("PLAYING")
                    mediaPlayer.pause()
                    myTimer.stopTimer()
                    updateWidgetImage(context, R.drawable.play, R.id.stopPlayButton)
                } else {
                    println("NOT PLAYING")
                    mediaPlayer.start()
                    myTimer.startTimer()
                    updateWidgetImage(context, R.drawable.pause_circle, R.id.stopPlayButton)
                }

                println("Progress: ${mediaPlayer.currentPosition}, ${mediaPlayer.duration} ${mediaPlayer.timestamp}")
                Log.i("Message", "PLAY_STOP_ACTION received")
            }
        }
    }

    private fun updateWidgetImage(context: Context, imageResource: Int, viewElement: Int) {
        val widgetManager = AppWidgetManager.getInstance(context)
        val widgetIds =
            widgetManager.getAppWidgetIds(ComponentName(context, MediaWidget::class.java))
        val views = RemoteViews(context.packageName, R.layout.main_widget)

        views.setTextViewText(R.id.artist_name, listMusic[getCurrentIndex()].artistName)
        views.setTextViewText(R.id.song_name, listMusic[getCurrentIndex()].songName)
        views.setImageViewResource(
            viewElement,
            imageResource
        )
        for (widgetId in widgetIds) {
            widgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun openChromeWithUrl(context: Context, url: String?) {
        url.let {
            try {
                val youtubePackageName = "com.google.android.youtube"
                val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                youtubeIntent.setPackage(youtubePackageName)
                youtubeIntent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                context.startActivity(youtubeIntent)
            } catch (e: Exception) {
                Log.e("MediaWidget", "Error opening YouTube URL: ${e.message}")
            }
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
    ) {
        mediaPlayer = MediaPlayer.create(context, listMusic[getCurrentIndex()].musicID).also {
            it.setOnCompletionListener {
                val intentOnComplete = Intent(context, MediaWidget::class.java)
                intentOnComplete.action = "RIGHT_ACTION"
                context.sendBroadcast(intentOnComplete)
            }
        }
        val widgetText = context.getString(R.string.appwidget_text)
        val views = RemoteViews(context.packageName, R.layout.main_widget)
        views.setTextViewText(androidx.core.R.id.text, widgetText)
        views.setTextViewText(R.id.artist_name, listMusic[getCurrentIndex()].artistName)
        views.setTextViewText(R.id.song_name, listMusic[getCurrentIndex()].songName)

        val timerViews = RemoteViews(context.packageName, R.layout.main_widget)

        myTimer = MyTimer(duration = mediaPlayer.duration.toLong(),
            onTick = { progress ->
                timerViews.setProgressBar(
                    R.id.progressBar,
                    mediaPlayer.duration,
                    mediaPlayer.currentPosition,
                    false
                )
                appWidgetManager.updateAppWidget(appWidgetId, timerViews)
                println("Progress: $progress%")
            }, onFinish = {
                println("Timer finished!")
            })

        ElementSetOnClickListener(context, "URL_ACTION", appWidgetId, views, R.id.imageView)
        ElementSetOnClickListener(context, "LEFT_ACTION", appWidgetId, views, R.id.leftButton)
        ElementSetOnClickListener(context, "RIGHT_ACTION", appWidgetId, views, R.id.rightButton)
        ElementSetOnClickListener(context, "PLAY_STOP_ACTION", appWidgetId, views, R.id.stopPlayButton)
        ElementSetOnClickListener(context, "RESET_ACTION", appWidgetId, views, R.id.resetButton)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun ElementSetOnClickListener(
        context: Context,
        actionString: String,
        requestCode: Int,
        views: RemoteViews,
        elementID: Int
    ) {
        val actionOnCLickIntent = Intent(context, MediaWidget::class.java)
        actionOnCLickIntent.action = actionString
        val actionOnClickPendingIntent = PendingIntent.getBroadcast(
            context, requestCode, actionOnCLickIntent, PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(elementID, actionOnClickPendingIntent)
    }
}
