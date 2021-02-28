package com.smusic.model

import android.net.Uri


data class Song(
    val songID: Long, val songTitle: String,
   val artist: String, val songData: String, val dateAdded: Long, val uri: Uri?,
)

