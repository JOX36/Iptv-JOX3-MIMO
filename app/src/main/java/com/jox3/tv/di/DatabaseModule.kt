package com.jox3.tv.di

import android.content.Context
import androidx.room.Room
import com.jox3.tv.data.local.dao.*
import com.jox3.tv.data.local.database.Jox3Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Jox3Database =
        Room.databaseBuilder(context, Jox3Database::class.java, "jox3tv.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideServerAccountDao(db: Jox3Database): ServerAccountDao = db.serverAccountDao()

    @Provides
    fun provideLiveChannelDao(db: Jox3Database): LiveChannelDao = db.liveChannelDao()

    @Provides
    fun provideMovieDao(db: Jox3Database): MovieDao = db.movieDao()

    @Provides
    fun provideSeriesDao(db: Jox3Database): SeriesDao = db.seriesDao()

    @Provides
    fun provideCategoryDao(db: Jox3Database): CategoryDao = db.categoryDao()
}
