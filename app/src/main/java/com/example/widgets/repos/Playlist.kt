package com.example.widgets.repos

import com.example.widgets.R

class Playlist {

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

    companion object {
        var currentSongIndex: Int = 0
    }

    fun getCurrentSong(): Music {
        return this.listMusic[currentSongIndex]
    }

    fun previousSong() {
        currentSongIndex =
            (currentSongIndex - 1 + this.listMusic.size) % this.listMusic.size
        println(currentSongIndex)
    }

    fun nextSong() {
        currentSongIndex = (currentSongIndex + 1) % this.listMusic.size
        println(currentSongIndex)
    }
}