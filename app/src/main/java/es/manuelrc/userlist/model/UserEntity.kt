package es.manuelrc.userlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import es.manuelrc.userlist.data.source.remote.dto.inner.Dob
import es.manuelrc.userlist.data.source.remote.dto.inner.Location
import es.manuelrc.userlist.data.source.remote.dto.inner.Picture

@Entity(tableName = "UserEntity")
class UserEntity(
    val gender: String,
    val name: String,
    val surname: String,
    @PrimaryKey(autoGenerate = false) val email: String,
    val picture: Picture,
    val phone: String,
    val location: Location,
    val registered: Dob,
    var isFavorite: Boolean = false
) {

    fun getFullName(): String {
        return "$name $surname"
    }
}