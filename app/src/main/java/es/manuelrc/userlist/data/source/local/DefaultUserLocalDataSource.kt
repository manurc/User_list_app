package es.manuelrc.userlist.data.source.local

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserDao
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.exceptions.TypeError
import io.reactivex.Observable
import javax.inject.Inject

class DefaultUserLocalDataSource @Inject constructor(private val userDao: UserDao) :
    UserLocalDataSource {


    override fun observeUsers(): Observable<Result.Success<List<UserEntity>>> =
         userDao.getAllUsers().map { userList ->
            Result.Success(userList)
        }.toObservable()

    override fun getUser(userEmail: String): Result<UserEntity> {

        return try {
            val user = userDao.findUser(userEmail).blockingGet()
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(DBException(TypeError.GET))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    override fun addNewUser(user: UserEntity) {
        try {
            userDao.addUsers(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.INSERT)
        }
    }

    override fun deleteUser(user: UserEntity) {
        try {
            userDao.deleteUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.DELETE)
        }
    }

    override fun updateUser(user: UserEntity) {
        try {
            userDao.updateUser(user)
        } catch (e: Exception) {
            e.printStackTrace()
            throw DBException(TypeError.UPDATE)
        }
    }

}