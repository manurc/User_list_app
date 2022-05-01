package es.manuelrc.userlist.data.source.remote

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultUserRemoteDataSource @Inject constructor(private val userApiClient: UserApiClient) :
    UserRemoteDataSource {

    override suspend fun getUsers(amount: Int): Result<List<UserEntity>> {
        return withContext(Dispatchers.IO){
            try {
                val response = userApiClient.getUsers(amount)
                if(response.isSuccessful){
                    val users= response.body()?.results ?: emptyList()
                    Result.Success(users.map {
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
                    })
                }else{
                    Result.Error(ApiResponseException(response.code()))
                }
            }catch (e:Exception){
                Result.Error(e)
            }
        }
    }
}