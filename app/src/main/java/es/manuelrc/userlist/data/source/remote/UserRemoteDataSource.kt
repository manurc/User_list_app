package es.manuelrc.userlist.data.source.remote

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity

interface UserRemoteDataSource {

    suspend fun getUsers(amount: Int): Result<List<UserEntity>>

}