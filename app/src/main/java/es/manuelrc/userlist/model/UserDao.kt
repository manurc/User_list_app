package es.manuelrc.userlist.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity where email like :email")
    fun findUser(email:String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addUsers(vararg userEntity: UserEntity?)

    @Delete
    suspend fun deleteUser(userEntity: UserEntity): Int

    @Update
    suspend fun updateUser(userEntity: UserEntity): Int

}


