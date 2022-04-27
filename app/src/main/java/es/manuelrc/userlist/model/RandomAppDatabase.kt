package es.manuelrc.userlist.model

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.manuelrc.userlist.model.converters.DobConverter
import es.manuelrc.userlist.model.converters.LocationConverter
import es.manuelrc.userlist.model.converters.PictureConverter

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
@TypeConverters(value = [PictureConverter::class, LocationConverter::class, DobConverter::class])
abstract class RandomAppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}