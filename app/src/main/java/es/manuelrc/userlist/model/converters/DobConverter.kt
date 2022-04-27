package es.manuelrc.userlist.model.converters


import androidx.room.TypeConverter
import com.google.gson.Gson
import es.manuelrc.userlist.data.source.remote.dto.inner.Dob

class DobConverter {

    private val gson: Gson = Gson()

    @TypeConverter
    fun storedStringToDob(value: String): Dob =
        if (value.isEmpty()) Dob("", 0L) else gson.fromJson(value, Dob::class.java)

    @TypeConverter
    fun dobToStoredString(dob: Dob): String = gson.toJson(dob)
}