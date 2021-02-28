package com.smusic.domain.usecases

import com.smusic.model.Song

interface GetLocalSongsUseCase {
    operator fun invoke():List<Song>
}