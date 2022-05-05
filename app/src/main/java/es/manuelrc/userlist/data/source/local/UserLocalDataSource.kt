package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

interface UserLocalDataSource {

    fun observeUsers(): Observable<Result.Success<List<UserEntity>>>

    fun getUser(userEmail: String): Result<UserEntity>

    fun addNewUser(user: UserEntity)

    fun deleteUser(user: UserEntity)

    fun updateUser(user: UserEntity)
}