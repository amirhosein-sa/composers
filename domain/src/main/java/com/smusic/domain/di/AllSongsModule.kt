package com.smusic.domain.di

import com.smusic.data.allSongs.AllSongsLocalDataSource
import com.smusic.data.allSongs.AllSongsLocalDataSourceImpl
import com.smusic.data.allSongs.AllSongsRepository
import com.smusic.data.allSongs.AllSongsRepositoryImpl
import com.smusic.domain.usecases.GetLocalSongsUseCase
import com.smusic.domain.usecases.GetLocalSongsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AllSongsBindingManager {
    @Binds
    abstract fun bindAllSongsDataSource(impl:AllSongsLocalDataSourceImpl):AllSongsLocalDataSource
    @Binds
    abstract fun bindAllSongsRepositoryImpl(impl:AllSongsRepositoryImpl):AllSongsRepository
    @Binds
    abstract fun bindLocalSongsUseCase(impl:GetLocalSongsUseCaseImpl):GetLocalSongsUseCase
}

@Module
@InstallIn(SingletonComponent::class)
object AllSongsRepo {
    @Provides
    fun provideAllSongsRepository(dataSource: AllSongsLocalDataSource) = AllSongsRepositoryImpl(dataSource)
}

