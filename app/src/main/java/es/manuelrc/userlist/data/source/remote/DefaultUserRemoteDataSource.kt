package es.manuelrc.userlist.data.source.remote

import com.skydoves.sandwich.*
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DefaultUserRemoteDataSource @Inject constructor(private val userApiClient: UserApiClient) :
    UserRemoteDataSource {

    override suspend fun getUsers(amount: Int): Flow<Result<List<UserEntity>>> = flow {

        userApiClient.getUsers(amount).suspendOnSuccess {
            val users = this.data.results
            emit(Result.Success(users.map {
                UserEntity(
                    it.gender,
                    it.name.first,
                    it.name.last,
                    it.email,
                    it.picture,
                    it.phone,
                    it.location,
                    it.registered,
                    false
                )
            }))
        }.suspendOnError {
            emit(Result.Error(ApiResponseException(this.response.code())))
        }.suspendOnException {
            emit(Result.Error(this.exception))
        }

    }
}