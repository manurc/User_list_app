package es.manuelrc.userlist.di

import android.content.Context
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.room.Room
import dagger.Module
import dagger.Provides
import es.manuelrc.userlist.model.RandomAppDatabase
import es.manuelrc.userlist.model.UserDao

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideChannelDao(appDatabase: RandomAppDatabase): UserDao = appDatabase.userDao()

    @Provides
    @Singleton
    fun provideRandomAppDatabase(@ApplicationContext appContext: Context?): RandomAppDatabase =
        Room.databaseBuilder(appContext!!, RandomAppDatabase::class.java, "myDB")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()

}