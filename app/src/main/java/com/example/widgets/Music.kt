package com.example.widgets

class Music(var artistName: String,
            var songName: String,
            var musicID: Int,
            var imageID: Int,
            var URL: String) {

    override fun toString(): String {
        println("name:${artistName} - ${songName} Id: ${musicID}, ImageId: ${imageID}, URL: ${URL}")
        return super.toString()
    }
}