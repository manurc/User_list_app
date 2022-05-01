package es.manuelrc.userlist.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import es.manuelrc.userlist.R
import es.manuelrc.userlist.adapters.UserListAdapter
import es.manuelrc.userlist.data.source.DefaultUserRepository
import es.manuelrc.userlist.data.source.local.DefaultUserLocalDataSource
import es.manuelrc.userlist.data.source.remote.DefaultUserRemoteDataSource
import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import es.manuelrc.userlist.data.source.remote.dto.inner.*
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserListInteractor
import es.manuelrc.userlist.util.MockUserApiClient
import es.manuelrc.userlist.util.MockUserDao
import es.manuelrc.userlist.util.launchFragmentInHiltContainer
import es.manuelrc.userlist.viewmodels.UserListViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class UserListFragmentTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)


    @Before
    fun init() {
        hiltRule.inject()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object TestAppModule {

        @Singleton
        @Provides
        fun provideUserListViewModel(): UserListViewModel {
            val apiUser = mutableListOf(
                User(
                    "f", Name("user3", "surname3"),
                    Location(
                        Street(0L, ""),
                        "", "", Coordinates("", "")
                    ),
                    Dob("", 0L), "email3", "phone3",
                    Picture("", "", ""),
                )
            )
            val localUser = mutableListOf(
                UserEntity(
                    "f", "user1", "surname1", "email1",
                    Picture("", "", ""), "phone1", Location(
                        Street(0L, ""),
                        "", "", Coordinates("", "")
                    ), Dob("", 0L), false
                ),
                UserEntity(
                    "m", "user2", "surname2", "email2",
                    Picture("", "", ""), "phone2", Location(
                        Street(12, "Plaza castilla"),
                        "Madrid", "Spain", Coordinates("", "")
                    ), Dob("", 0L), false
                )
            )
            val userApiClient = MockUserApiClient(200, UsersResponse(apiUser))
            val userDao = MockUserDao(localUser)
            val userRepository = DefaultUserRepository(
                DefaultUserRemoteDataSource(userApiClient),
                DefaultUserLocalDataSource(userDao)
            )
            val userListInteractor = UserListInteractor(userRepository)
            return UserListViewModel(userListInteractor)
        }
    }

    @Test
    fun testView() {

        val bundle = Bundle()
        launchFragmentInHiltContainer<UserListFragment>(bundle, R.style.Theme_UserList)
        Espresso.onView(withId(R.id.recyclerview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.recyclerview)).perform(
            RecyclerViewActions.actionOnItemAtPosition<UserListAdapter.ViewHolder>(
                0,
                clickOnViewChild(R.id.cbFavorite)
            )
        )
        Espresso.onView(withId(R.id.recyclerview)).perform(
            RecyclerViewActions.actionOnItemAtPosition<UserListAdapter.ViewHolder>(
                0,
                clickOnViewChild(R.id.cbDelete)
            )
        )
        Espresso.onView(withId(R.id.fab)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.action_search))
            .perform(ViewActions.click(), ViewActions.typeText("user3"))
            .perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER))
        Espresso.onView(withId(R.id.recyclerview))
            .perform(
                RecyclerViewActions.actionOnItem<UserListAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText("user3 surname3")),
                    ViewActions.click()
                )
            )
        Espresso.onView(withId(R.id.recyclerview)).perform(
            RecyclerViewActions.actionOnItemAtPosition<UserListAdapter.ViewHolder>(
                0,
                clickOnViewChild(R.id.imgPhoto)
            )
        )

    }

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) =
            ViewActions.click().perform(uiController, view.findViewById(viewId))
    }
}