package com.cs211d.musicplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song (
    @PrimaryKey(autoGenerate = true)
    var ID: Long = 0,

    var ALBUM: String,

    var TITLE: String,

    var ARTIST: String,

    var FILENAME: String,

    var PATH: String)