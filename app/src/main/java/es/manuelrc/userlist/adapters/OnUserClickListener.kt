package es.manuelrc.userlist.adapters

import es.manuelrc.userlist.model.UserEntity

interface OnUserClickListener {
    fun onFavoriteUser(user: UserEntity)
    fun onDeleteUser(user: UserEntity)
}