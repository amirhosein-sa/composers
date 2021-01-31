package com.smusic.base_android

import android.net.Uri


data class Songs(
    val songID: Long, val songTitle: String,
    val artist: String, val songData: String, val dateAdded: Long, val uri: Uri?,
)

