package es.manuelrc.userlist.model.converters


import androidx.room.TypeConverter
import com.google.gson.Gson
import es.manuelrc.userlist.data.source.remote.dto.inner.Coordinates
import es.manuelrc.userlist.data.source.remote.dto.inner.Location
import es.manuelrc.userlist.data.source.remote.dto.inner.Street

class LocationConverter {

    private val gson: Gson = Gson()

    @TypeConverter
    fun storedStringToLocation(value: String): Location =
        if (value.isEmpty()) Location(
            Street(0, ""),
            "",
            "",
            Coordinates("", "")
        ) else gson.fromJson(value, Location::class.java)


    @TypeConverter
    fun locationToStoredString(location: Location): String = gson.toJson(location)
}