package es.manuelrc.userlist.view.utils

import es.manuelrc.userlist.model.UserEntity

object UserPrinter {

    fun printUserFullName(user: UserEntity?): String {
        return "${user?.name} ${user?.surname}"
    }
}