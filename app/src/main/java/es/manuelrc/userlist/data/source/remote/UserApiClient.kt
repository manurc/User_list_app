package es.manuelrc.userlist.data.source.remote

import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiClient {
    @GET("/")
    suspend fun getUsers(@Query(value = "results") results: Int): Response<UsersResponse>
}
