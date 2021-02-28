package com.smusic.data.allSongs

import com.smusic.model.Song

interface AllSongsRepository{
     fun getLocalSongs():List<Song>
}

