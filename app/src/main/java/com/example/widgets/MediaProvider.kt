package com.example.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews

class MediaProvider : AppWidgetProvider() {

    companion object {
        const val PREFS_NAME = "WidgetPrefs"
        const val ARTIST_NAME_KEY = "artist_name"
        const val SONG_NAME_KEY = "song_name"
        const val PROGRESS_KEY = "progress"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        saveState(context)
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int, newOptions: Bundle
    ) {
        restoreState(context)

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    private fun saveState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(ARTIST_NAME_KEY, "YourArtistName") // Replace with actual artist name
        editor.putString(SONG_NAME_KEY, "YourSongName") // Replace with actual song name
        editor.putInt(PROGRESS_KEY, 50) // Replace with actual progress
        editor.apply()
    }

    private fun restoreState(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val artistName = prefs.getString(ARTIST_NAME_KEY, "")
        val songName = prefs.getString(SONG_NAME_KEY, "")
        val progress = prefs.getInt(PROGRESS_KEY, 0)

        val views = RemoteViews(
            context.packageName,
            R.layout.main_widget
        )
        views.setTextViewText(R.id.artist_name, artistName)
        views.setTextViewText(R.id.song_name, songName)
        views.setProgressBar(R.id.progressBar, 100, progress, false)
    }
}