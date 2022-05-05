package es.manuelrc.userlist.data.source

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.local.UserLocalDataSource
import es.manuelrc.userlist.data.source.remote.UserRemoteDataSource
import es.manuelrc.userlist.model.UserEntity
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {


    override fun observeUsers(): Observable<Result.Success<List<UserEntity>>> {
        return userLocalDataSource.observeUsers()
    }

    override fun getUser(userEmail: String): Result<UserEntity> {
        return userLocalDataSource.getUser(userEmail)
    }

    override suspend fun addNewUsers(amount: Int) {
        val remoteUsers = userRemoteDataSource.getUsers(amount)
        if (remoteUsers is Result.Success) {
            remoteUsers.data.forEach {
                saveUser(it)
            }
        } else if (remoteUsers is Result.Error) {
            throw remoteUsers.exception
        }
    }

    override fun deleteUser(user: UserEntity) {


        userLocalDataSource.deleteUser(user)


    }

    override fun updateUser(user: UserEntity) {

        userLocalDataSource.updateUser(user)

    }

    override fun saveUser(user: UserEntity) {

        userLocalDataSource.addNewUser(user)

    }
}