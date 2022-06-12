package es.manuelrc.userlist.util

import com.google.gson.Gson
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import es.manuelrc.userlist.data.source.remote.UserApiClient
import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class MockUserApiClient(private val code: Int, private val response: UsersResponse) :
    UserApiClient {

    override suspend fun getUsers(results: Int): ApiResponse<UsersResponse> {
        return if (code in 200..300) {
            ApiResponse.of(SandwichInitializer.successCodeRange) {
                Response.success(response)
            }
        } else {
            ApiResponse.of(SandwichInitializer.successCodeRange) {
                Response.error(code, Gson().toJson(response).toResponseBody())
            }
        }
    }
}