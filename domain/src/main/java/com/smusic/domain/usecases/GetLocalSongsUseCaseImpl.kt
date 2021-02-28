package com.smusic.domain.usecases

import com.smusic.data.allSongs.AllSongsRepository
import javax.inject.Inject

class GetLocalSongsUseCaseImpl @Inject constructor(private val repo:AllSongsRepository): GetLocalSongsUseCase {
    override fun invoke() = repo.getLocalSongs()
}