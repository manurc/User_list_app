package es.manuelrc.userlist.util

import com.google.gson.Gson
import es.manuelrc.userlist.data.source.remote.UserApiClient
import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockUserApiClient(private val code:Int, private val response: UsersResponse): UserApiClient {

    override suspend fun getUsers(results: Int): Response<UsersResponse> {
        return if(code in 200..300){
            Response.success(response)
        }else{
            Response.error(code, Gson().toJson(response).toResponseBody())
        }
    }
}