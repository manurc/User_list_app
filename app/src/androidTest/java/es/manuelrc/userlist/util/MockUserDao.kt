package es.manuelrc.userlist.util


import es.manuelrc.userlist.model.UserDao
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MockUserDao(private val localUsers: MutableList<UserEntity>) : UserDao {

    override fun getAllUsers(): Flow<List<UserEntity>> {
        return flow {
            emit(localUsers)
        }
    }

    override fun findUser(email: String): UserEntity {
        return localUsers.last()
    }

    override suspend fun addUsers(vararg userEntity: UserEntity?) {
        userEntity.forEach {
            if (it != null)
                localUsers.add(it)
        }

    }

    override suspend fun deleteUser(userEntity: UserEntity): Int {
        localUsers.remove(userEntity)
        return 1
    }

    override suspend fun updateUser(userEntity: UserEntity): Int {
        localUsers.add(userEntity)
        return 1
    }
}