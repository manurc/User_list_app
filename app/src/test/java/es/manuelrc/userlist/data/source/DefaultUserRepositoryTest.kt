package es.manuelrc.userlist.data.source

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.exceptions.DBException
import es.manuelrc.userlist.data.source.local.UserLocalDataSource
import es.manuelrc.userlist.data.source.remote.ApiResponseException
import es.manuelrc.userlist.data.source.remote.UserRemoteDataSource
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.exceptions.TypeError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultUserRepositoryTest : TestCase() {

    @MockK
    lateinit var userRemoteDataSource: UserRemoteDataSource

    @MockK
    lateinit var userLocalDataSource: UserLocalDataSource

    // Class under test
    private lateinit var userRepository: DefaultUserRepository

    @Before
    override fun setUp() {
        MockKAnnotations.init(this)
        userRepository = DefaultUserRepository(userRemoteDataSource, userLocalDataSource)
    }

    @After
    fun onAfter() {
        unmockkAll()
    }

    @Test
    fun testEmptyLocalDataSource() = runTest(dispatchTimeoutMs = 2_000) {
        val exceptionResult = DBException(TypeError.GET)
        coEvery { userLocalDataSource.getUser(any()) } returns Result.Error(exceptionResult)
        val result = userRepository.getUser("userEmail")
        assertNotNull(result)
        assert(result is Result.Error && result.exception == exceptionResult)
    }

    @Test
    fun testEmptyRemoteDataSource() {
        coEvery { userRemoteDataSource.getUsers(any()) } returns Result.Error(
            ApiResponseException(
                400
            )
        )
        Assert.assertThrows(ApiResponseException::class.java) {
            runTest {
                userRepository.addNewUsers(1)
            }

        }
    }

    @Test
    fun testGetUser() = runTest {
        val user = mockk<UserEntity>()
        coEvery { userLocalDataSource.getUser(any()) } returns Result.Success(user)
        val result = userRepository.getUser("userEmail")
        assertNotNull(result)
        assert(result is Result.Success && result.data == user)
    }

    @Test
    fun testAddUser() = runTest {
        val fakeLocalDataStore = mutableListOf<UserEntity>()
        val user = listOf(mockk<UserEntity>())
        coEvery { userRemoteDataSource.getUsers(any()) } returns Result.Success(user)
        coEvery { userLocalDataSource.addNewUser(any()) } coAnswers {
            user.forEach {
                fakeLocalDataStore.add(
                    it
                )
            }
        }
        userRepository.addNewUsers(1)
        assert(fakeLocalDataStore.containsAll(user))

    }


}