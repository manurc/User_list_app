package es.manuelrc.userlist.data.source.remote.dto.inner

data class Location(
    val street: Street,
    val city: String,
    val state: String,
    val coordinates: Coordinates
) {

    override fun toString(): String {
        return "${street.name} ${street.number}, $city, $state"
    }
}


