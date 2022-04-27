package es.manuelrc.userlist.data.source.remote.dto.inner

data class Street(
    val number: Long,
    val name: String
) {
    override fun toString(): String {
        return " number=$number, name='$name')"
    }
}
