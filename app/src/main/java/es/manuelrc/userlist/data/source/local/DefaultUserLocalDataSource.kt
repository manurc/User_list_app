package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserDao
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.exceptions.TypeError
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
                    return@withContext Result.Error(DBException(TypeError.GET))
                }
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }


    override suspend fun addNewUser(user: UserEntity) = withContext(Dispatchers.IO) {
        try {
            userDao.addUsers(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.INSERT)
        }

    }

    override suspend fun deleteUser(user: UserEntity) = withContext<Unit>(Dispatchers.IO) {
        try {
            userDao.deleteUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.DELETE)
        }

    }

    override suspend fun updateUser(user: UserEntity) = withContext<Unit>(Dispatchers.IO) {
        try {
            userDao.updateUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.UPDATE)
        }

    }

}