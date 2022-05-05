package es.manuelrc.userlist.viewmodels

import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.FilterConstrains
import es.manuelrc.userlist.data.source.remote.dto.inner.*
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserListInteractor
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class UserListViewModelTest : TestCase() {

    @MockK
    lateinit var userListInteractor: UserListInteractor

    // Class under test
    private lateinit var userListViewModel: UserListViewModel

    private lateinit var apiUsersList: MutableList<UserEntity>


    @Before
    override fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        apiUsersList = mutableListOf()
        coEvery { userListInteractor.observeUsers } answers { Observable.fromArray( Result.Success(apiUsersList)) }
        userListViewModel = UserListViewModel(userListInteractor)
    }

    @After
    fun onAfter() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun testLoadUserEmptyDB() = runBlocking {
        coEvery { userListInteractor.loadUsers() } answers {}
        userListViewModel.loadUsers()
        val users = userListViewModel.mUsers.blockingLast()
        assert(users is Result.Success && users.data.isEmpty())

    }

    @Test
    fun testLoadUser() = runBlocking {
        val user = mockk<UserEntity>()
        apiUsersList.add(user)
        coEvery { userListInteractor.loadUsers() } answers {}
        userListViewModel.loadUsers()
        val users = userListViewModel.mUsers.blockingLast()
        assert(users is Result.Success && users.data.isNotEmpty() && users.data.contains(user))

    }

    @Test
    fun testDeleteUser() = runBlocking {
        val user = mockk<UserEntity>()
        apiUsersList.add(user)
        coEvery { userListInteractor.deleteUsers(any()) } answers { apiUsersList.remove(user) }
        userListViewModel.deleteUser(user)
        val users = userListViewModel.mUsers.blockingLast()
        assert(users is Result.Success && users.data.isEmpty())

    }

    @Test
    fun testUpdateUser() = runBlocking {
        val user = UserEntity(
            "f", "user1", "surname1", "email1",
            Picture("", "", ""), "phone1", Location(
                Street(0L, ""),
                "", "", Coordinates("", "")
            ), Dob("", 0L), false
        )
        apiUsersList.add(user)
        coEvery { userListInteractor.updateUsers(any()) } answers {
            apiUsersList.find { it == user }?.isFavorite = true
        }
        userListViewModel.updateUser(user)
        val users = userListViewModel.mUsers.blockingLast()
        assert(users is Result.Success && users.data.isNotEmpty() && users.data[0].isFavorite)

    }

    @Test
    fun testAddUser() = runBlocking {
        val user = mockk<UserEntity>()
        coEvery { userListInteractor.addNewUser(any()) } answers { apiUsersList.add(user) }
        userListViewModel.addUser()
        val users = userListViewModel.mUsers.blockingLast()
        assert(users is Result.Success && users.data.isNotEmpty() && users.data[0] == user)

    }

    @Test
    fun testFilterUser() = runBlocking {
        val user1 = UserEntity(
            "f", "user1", "surname1", "email1",
            Picture("", "", ""), "phone1", Location(
                Street(0L, ""),
                "", "", Coordinates("", "")
            ), Dob("", 0L), false
        )
        val user2 = UserEntity(
            "m", "user2", "surname2", "email2",
            Picture("", "", ""), "phone2", Location(
                Street(0L, ""),
                "", "", Coordinates("", "")
            ), Dob("", 0L), true
        )
        apiUsersList.add(user1)
        apiUsersList.add(user2)
        coEvery {
            userListInteractor.updateFilter(
                any(), any(), any(),
                any(), any()
            )
        } answers { apiUsersList.removeAll(apiUsersList.filter { !it.isFavorite }.toMutableList())  }
        userListViewModel.filterUsers(FilterConstrains.OrderedEnum.GENDER, isFavorite = true)
        val users = userListViewModel.mUsers.blockingLast()
        val sortType = userListViewModel.sortType.value
        assert(users is Result.Success && users.data.isNotEmpty() && users.data.all { it.isFavorite }
                && sortType.peekContent() == FilterConstrains.OrderedEnum.GENDER)

    }


}