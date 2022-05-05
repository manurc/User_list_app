package es.manuelrc.userlist.util


import es.manuelrc.userlist.model.UserDao
import es.manuelrc.userlist.model.UserEntity
import io.reactivex.Flowable
import io.reactivex.Single


class MockUserDao(private val localUsers: MutableList<UserEntity>) : UserDao {

    override fun getAllUsers(): Flowable<List<UserEntity>> {
        return Flowable.fromArray(localUsers)
    }

    override fun findUser(email: String): Single<UserEntity?> {
        return Single.fromCallable { localUsers.last() }
    }

    override  fun addUsers(vararg userEntity: UserEntity?) {
        userEntity.forEach {
            if (it != null)
                localUsers.add(it)
        }

    }

    override  fun deleteUser(userEntity: UserEntity): Int {
        localUsers.remove(userEntity)
        return 1
    }

    override  fun updateUser(userEntity: UserEntity): Int {
        localUsers.add(userEntity)
        return 1
    }
}