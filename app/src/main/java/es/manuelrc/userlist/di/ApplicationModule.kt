package es.manuelrc.userlist.di

import android.content.Context
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import es.manuelrc.userlist.data.source.DefaultUserRepository
import es.manuelrc.userlist.data.source.UserRepository
import es.manuelrc.userlist.data.source.local.DefaultUserLocalDataSource
import es.manuelrc.userlist.data.source.local.UserLocalDataSource
import es.manuelrc.userlist.data.source.remote.DefaultUserRemoteDataSource
import es.manuelrc.userlist.data.source.remote.UserApiClient
import es.manuelrc.userlist.data.source.remote.UserRemoteDataSource
import es.manuelrc.userlist.model.UserDao

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideUserLocalDatasource(userDao: UserDao): UserLocalDataSource =
        DefaultUserLocalDataSource(userDao)

    @Provides
    fun provideUserRemoteDatasource(userApiClient: UserApiClient): UserRemoteDataSource =
        DefaultUserRemoteDataSource(userApiClient)

    @Provides
    fun provideUserRepository(
        userDataSource: UserRemoteDataSource,
        userLocalDataSource: UserLocalDataSource
    ): UserRepository =
        DefaultUserRepository(userDataSource, userLocalDataSource)

}