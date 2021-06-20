package com.example.theproductivityapp.di

import android.content.Context
import androidx.room.Room
import com.example.theproductivityapp.db.RunningDB
import com.example.theproductivityapp.db.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.managers.ApplicationComponentManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RunningDB::class.java,
        Utils.DB_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideTodoDao(database: RunningDB) = database.getDao()

}