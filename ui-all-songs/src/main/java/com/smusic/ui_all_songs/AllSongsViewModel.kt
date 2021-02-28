package com.smusic.ui_all_songs

import androidx.lifecycle.ViewModel
import com.smusic.domain.usecases.GetLocalSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AllSongsViewModel @Inject constructor(
     private val getLocalSongsUseCase: GetLocalSongsUseCase
) : ViewModel() {
     fun getLocalSongs() = getLocalSongsUseCase()
}