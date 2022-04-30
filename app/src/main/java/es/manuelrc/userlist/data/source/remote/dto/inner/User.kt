package es.manuelrc.userlist.data.source.remote.dto.inner

data class User(
    val gender: String,
    val name: Name,
    val location: Location,
    val registered: Dob,
    val email: String,
    val phone: String,
    val picture: Picture,
)
