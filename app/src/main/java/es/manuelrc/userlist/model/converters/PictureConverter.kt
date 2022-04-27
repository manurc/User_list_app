package es.manuelrc.userlist.model.converters


import androidx.room.TypeConverter
import com.google.gson.Gson
import es.manuelrc.userlist.data.source.remote.dto.inner.Picture

class PictureConverter {

    private val gson: Gson = Gson()

    @TypeConverter
    fun storedStringToPicture(value: String): Picture =
        if (value.isEmpty()) Picture("", "", "") else gson.fromJson(value, Picture::class.java)

    @TypeConverter
    fun pictureToStoredString(picture: Picture): String = gson.toJson(picture)
}