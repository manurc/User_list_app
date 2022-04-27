package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    fun observeUsers(): Flow<Result<List<UserEntity>>>

    suspend fun getUser(userEmail: String): Result<UserEntity>

    suspend fun addNewUser(user: UserEntity)

    suspend fun deleteUser(user: UserEntity)

    suspend fun updateUser(user: UserEntity)
}