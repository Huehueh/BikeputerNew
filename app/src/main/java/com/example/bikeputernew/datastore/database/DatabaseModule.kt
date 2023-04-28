package com.example.bikeputernew.datastore.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.example.bikeputernew.values.Constants
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BikeDatabase
            = Room.databaseBuilder(
        context,
        BikeDatabase::class.java,
        Constants.DATABASE_NAME
    ).allowMainThreadQueries().build()

    @Singleton
    @Provides
    fun provideDao(database: BikeDatabase): TripDao = database.tripDao()
}