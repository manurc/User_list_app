package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserDao
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserLocalDataSource @Inject constructor(private val userDao: UserDao) :
    UserLocalDataSource {


    override fun observeUsers(): Flow<Result<List<UserEntity>>> =
        userDao.getAllUsers().map { userList ->
            Result.Success(userList)
        }


    override suspend fun getUser(userEmail: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                val user = userDao.findUser(userEmail)
                if (user != null) {
                    return@withContext Result.Success(user)
                } else {
                    return@withContext Result.Error(DBException(DBExceptionType.USER_NOT_FOUND))
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }


    override suspend fun addNewUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.addUsers(user)
    }

    override suspend fun deleteUser(user: UserEntity) = withContext<Unit>(Dispatchers.IO) {
        userDao.deleteUser(user)
    }

    override suspend fun updateUser(user: UserEntity) = withContext<Unit>(Dispatchers.IO) {
        userDao.updateUser(user)
    }

}