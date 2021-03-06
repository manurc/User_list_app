package es.manuelrc.userlist.model.interactors

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.UserRepository
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDetailsInteractor @Inject constructor(private val userRepository: UserRepository) {

    suspend fun findUser(email: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        userRepository.getUser(email)
    }
}