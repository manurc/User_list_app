package es.manuelrc.userlist.data.source

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun observeUsers(): Observable<Result.Success<List<UserEntity>>>

    fun getUser(userEmail: String): Result<UserEntity>

    suspend fun addNewUsers(amount: Int)

    fun deleteUser(user: UserEntity)

    fun updateUser(user: UserEntity)

    fun saveUser(user: UserEntity)

}