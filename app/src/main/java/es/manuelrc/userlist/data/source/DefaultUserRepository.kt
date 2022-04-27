package es.manuelrc.userlist.data.source

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.local.UserLocalDataSource
import es.manuelrc.userlist.data.source.remote.UserRemoteDataSource
import es.manuelrc.userlist.model.UserEntity
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


    override fun observeUsers(): Flow<Result<List<UserEntity>>> {
        return userLocalDataSource.observeUsers()
    }

    override suspend fun getUser(userEmail: String): Result<UserEntity> {
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

    override suspend fun deleteUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { userLocalDataSource.deleteUser(user) }
            }
        }
    }

    override suspend fun updateUser(user: UserEntity) {
        withContext(Dispatchers.IO) {
            coroutineScope {
                launch { userLocalDataSource.updateUser(user) }
            }
        }
    }

    override suspend fun saveUser(user: UserEntity){
        coroutineScope {
            launch { userLocalDataSource.addNewUser(user) }
        }
    }
}