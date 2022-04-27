package es.manuelrc.userlist.data.source

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun observeUsers(): Flow<Result<List<UserEntity>>>

    suspend fun getUser(userEmail: String): Result<UserEntity>

    suspend fun addNewUsers(amount: Int)

    suspend fun deleteUser(user: UserEntity)

    suspend fun updateUser(user: UserEntity)

    suspend fun saveUser(user: UserEntity)

}