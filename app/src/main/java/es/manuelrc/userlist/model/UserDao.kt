package es.manuelrc.userlist.model

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flowable<List<UserEntity>>

    @Query("SELECT * FROM UserEntity where email like :email")
    fun findUser(email: String): Single<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addUsers(vararg userEntity: UserEntity?)

    @Delete
    fun deleteUser(userEntity: UserEntity): Int

    @Update
    fun updateUser(userEntity: UserEntity): Int

}


