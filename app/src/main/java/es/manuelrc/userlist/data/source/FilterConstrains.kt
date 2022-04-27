package es.manuelrc.userlist.data.source

import android.location.Location

class FilterConstrains(
    var order: OrderedEnum,
    var isFavorite: Boolean? = null,
    var isLocation: Boolean? = null,
    var currentLocation: Location? = null,
    var query:String = ""
) {
    fun copy():FilterConstrains= FilterConstrains(order,isFavorite,isLocation,currentLocation,query)

    enum class OrderedEnum {
        NAME,
        GENDER
    }
}