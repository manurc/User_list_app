package es.manuelrc.userlist.data.source.remote.dto.inner

data class Coordinates(
    val latitude: String,
    val longitude: String
) {
    override fun toString(): String {
        return "latitude='$latitude', longitude='$longitude'"
    }
}