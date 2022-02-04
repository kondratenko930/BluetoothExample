package com.example.bluetoothexample.di

import android.content.Context
import androidx.room.Room
import com.example.bluetoothexample.data.local.AppDatabase
import com.example.bluetoothexample.data.local.BTDeviceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



//@InstallIn(ApplicationComponent::class)
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app.db"
        ).build()
    }

    @Provides
    fun provideBTDeviceDao(appDatabase: AppDatabase): BTDeviceDao {
        return appDatabase.btdeviceDao()
    }
}