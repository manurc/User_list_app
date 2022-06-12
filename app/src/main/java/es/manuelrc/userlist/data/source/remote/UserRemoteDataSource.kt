package es.manuelrc.userlist.data.source.remote

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRemoteDataSource {

    suspend fun getUsers(amount: Int): Flow<Result<List<UserEntity>>>

}