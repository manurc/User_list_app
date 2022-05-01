package es.manuelrc.userlist.viewmodels

import es.manuelrc.userlist.R
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.remote.dto.inner.*
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserDetailsInteractor
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.Exception

@ExperimentalCoroutinesApi
class UserDetailViewModelTest : TestCase() {

    @MockK
    lateinit var userDetailsInteractor: UserDetailsInteractor

    // Class under test
    private lateinit var userDetailViewModel: UserDetailViewModel

    private lateinit var apiUsersList: MutableList<UserEntity>

    @Before
    override fun setUp() {
        MockKAnnotations.init(this)
        apiUsersList = mutableListOf()
        userDetailViewModel = UserDetailViewModel(userDetailsInteractor)
    }

    @After
    fun onAfter() {
        unmockkAll()
    }

    @Test
    fun testLoadUserSuccess() = runBlocking {
        val user = UserEntity(
            "f", "user1", "surname1", "email1",
            Picture("", "", ""), "phone1", Location(
                Street(0L, ""),
                "", "", Coordinates("", "")
            ), Dob("", 0L), false
        )
        coEvery { userDetailsInteractor.findUser(any()) } returns Result.Success(user)
        userDetailViewModel.findUser("userEmail")
        val userSelected = userDetailViewModel.userSelected.value
        val loading = userDetailViewModel.isLoading.value

        assert(userSelected == user && !loading)

    }

    @Test
    fun testLoadUserError() = runBlocking {
        val exception = Exception()
        coEvery { userDetailsInteractor.findUser(any()) } returns Result.Error(exception)
        userDetailViewModel.findUser("userEmail")
        val userSelected = userDetailViewModel.userSelected.value
        val loading = userDetailViewModel.isLoading.value
        val snackbarMessage = userDetailViewModel.snackbarMessage.value

        assert(userSelected == null && !loading && snackbarMessage.peekContent() == R.string.error_obtaining_from_db)

    }

}