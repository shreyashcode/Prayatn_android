package com.example.theproductivityapp.di

import android.content.Context
import androidx.room.Room
import com.example.theproductivityapp.db.RunningDB
import com.example.theproductivityapp.db.StandUpDB
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
        Utils.DB_TODO
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideStandUpDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        StandUpDB::class.java,
        Utils.DB_STAND_UP
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideTodoDao(database: RunningDB) = database.getDao()

    @Singleton
    @Provides
    fun provideStandUpDao(database: StandUpDB) = database.getDao();
}