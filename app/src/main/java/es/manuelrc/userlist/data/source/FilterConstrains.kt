package es.manuelrc.userlist.data.source

import android.location.Location

 data class FilterConstrains(
    var order: OrderedEnum,
    var isFavorite: Boolean? = null,
    var isLocation: Boolean? = null,
    var currentLocation: Location? = null,
    var query: String = ""
) {

    enum class OrderedEnum {
        NAME,
        GENDER
    }
}