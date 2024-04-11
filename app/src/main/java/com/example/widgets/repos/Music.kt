package com.example.widgets.repos

class Music(var artistName: String,
            var songName: String,
            var musicID: Int,
            var imageID: Int,
            var URL: String) {

    override fun toString(): String {
        println("name:${this.artistName} - ${this.songName} Id: ${this.musicID}, ImageId: ${this.imageID}, URL: $URL")
        return super.toString()
    }
}