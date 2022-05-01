package es.manuelrc.userlist.data.source.remote

import com.google.gson.Gson
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
class DefaultUserRemoteDataSourceTest: TestCase() {

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
        val response = Response.success(UsersResponse(userFromApi))
        coEvery { userApiClient.getUsers(any()) } returns response
        val result = userRemoteDataSource.getUsers(1)
        assertNotNull(result)
        assert(result is Result.Success && result.data.all {
            it.name == userParsed[0].name &&
                    it.gender == userParsed[0].gender &&
                    it.email == userParsed[0].email &&
                    it.location == userParsed[0].location &&
                    it.phone == userParsed[0].phone &&
                    it.picture == userParsed[0].picture &&
                    it.registered == userParsed[0].registered
        })
    }

    @Test
    fun testGetUsersHttpError() = kotlinx.coroutines.test.runTest(dispatchTimeoutMs = 2_000) {
        val serviceError = "generic error"
        val response = Response.error<UsersResponse>(400, Gson().toJson(serviceError)
            .toResponseBody("application/json; charset=utf-8".toMediaType()))
        coEvery { userApiClient.getUsers(any()) } returns response
        val result = userRemoteDataSource.getUsers(1)
        assertNotNull(result)
        assert(result is Result.Error && result.exception is ApiResponseException && (result.exception as ApiResponseException).code == 400)
    }

    @Test
    fun testGetUsersGenericException() = kotlinx.coroutines.test.runTest(dispatchTimeoutMs = 2_000) {
        val exception =  Exception()
        coEvery { userApiClient.getUsers(any()) } throws exception
        val result = userRemoteDataSource.getUsers(1)
        assertNotNull(result)
        assert(result is Result.Error && result.exception == exception)
    }

}