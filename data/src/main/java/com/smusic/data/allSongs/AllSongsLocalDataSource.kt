package com.smusic.data.allSongs

import com.smusic.model.Song

interface AllSongsLocalDataSource {
     fun getLocalSongs():List<Song>
}