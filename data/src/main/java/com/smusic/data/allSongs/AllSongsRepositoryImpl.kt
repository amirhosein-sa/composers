package com.smusic.data.allSongs

import com.smusic.model.Song
import javax.inject.Inject

class AllSongsRepositoryImpl @Inject constructor(
    private val localDataSource:AllSongsLocalDataSource
) : AllSongsRepository {
    override fun getLocalSongs() = localDataSource.getLocalSongs()
}