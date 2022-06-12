package es.manuelrc.userlist.data.source.remote

import com.google.gson.Gson
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.SandwichInitializer
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import es.manuelrc.userlist.data.source.remote.dto.inner.*
import es.manuelrc.userlist.model.UserEntity
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.lang.Exception

@ExperimentalCoroutinesApi
class DefaultUserRemoteDataSourceTest : TestCase() {

    @MockK
    lateinit var userApiClient: UserApiClient

    // Class under test
    private lateinit var userRemoteDataSource: DefaultUserRemoteDataSource

    private lateinit var userFromApi: List<User>

    private lateinit var userParsed: List<UserEntity>

    @Before
    override fun setUp() {
        MockKAnnotations.init(this)
        userRemoteDataSource = DefaultUserRemoteDataSource(userApiClient)

        userFromApi = listOf(
            User(
                "m", Name("user2", "surname2"), Location(
                    Street(0L, ""),
                    "", "", Coordinates("", "")
                ), Dob("", 0L), "email2", "phone2", Picture("", "", "")

            )
        )

        userParsed = listOf(
            UserEntity(
                "m", "user2", "surname2", "email2",
                Picture("", "", ""), "phone2", Location(
                    Street(0L, ""),
                    "", "", Coordinates("", "")
                ), Dob("", 0L), false
            )
        )
    }

    @After
    fun onAfter() {
        unmockkAll()
    }

    @Test
    fun testGetUsersSuccess() = kotlinx.coroutines.test.runTest(dispatchTimeoutMs = 2_000) {

        val response =
            ApiResponse.of(SandwichInitializer.successCodeRange) {
                Response.success(
                    UsersResponse(
                        userFromApi
                    )
                )
            }
        coEvery { userApiClient.getUsers(any()) } returns response
        val result = userRemoteDataSource.getUsers(1)
        result.collect {
            assert(it is Result.Success && it.data.all { user ->
                user.name == userParsed[0].name &&
                        user.gender == userParsed[0].gender &&
                        user.email == userParsed[0].email &&
                        user.location == userParsed[0].location &&
                        user.phone == userParsed[0].phone &&
                        user.picture == userParsed[0].picture &&
                        user.registered == userParsed[0].registered
            })
        }


    }

    @Test
    fun testGetUsersHttpError() = kotlinx.coroutines.test.runTest(dispatchTimeoutMs = 2_000) {
        val serviceError = "generic error"
        val response =
            ApiResponse.of(SandwichInitializer.successCodeRange) {
                Response.error<UsersResponse>(
                    400, Gson().toJson(serviceError)
                        .toResponseBody("application/json; charset=utf-8".toMediaType()))
            }
        coEvery { userApiClient.getUsers(any()) } returns response
        val result = userRemoteDataSource.getUsers(1)
        assertNotNull(result)
        result.collect{
            assert(it is Result.Error && it.exception is ApiResponseException && (it.exception as ApiResponseException).code == 400)
        }
    }

    @Test
    fun testGetUsersGenericException() =
        kotlinx.coroutines.test.runTest(dispatchTimeoutMs = 122_000) {
            val exception = Exception()
            val apiResponse = ApiResponse.error<UsersResponse>(exception)
            coEvery { userApiClient.getUsers(any()) } returns apiResponse
            val result = userRemoteDataSource.getUsers(1)
            result.collect{
                assert(it is Result.Error && it.exception == exception)
            }
        }

}