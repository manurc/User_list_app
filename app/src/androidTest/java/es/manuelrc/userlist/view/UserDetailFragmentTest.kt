package es.manuelrc.userlist.view

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.components.SingletonComponent
import es.manuelrc.userlist.R
import es.manuelrc.userlist.data.source.DefaultUserRepository
import es.manuelrc.userlist.data.source.local.DefaultUserLocalDataSource
import es.manuelrc.userlist.data.source.remote.DefaultUserRemoteDataSource
import es.manuelrc.userlist.data.source.remote.dto.UsersResponse
import es.manuelrc.userlist.data.source.remote.dto.inner.*
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserDetailsInteractor
import es.manuelrc.userlist.util.MockUserApiClient
import es.manuelrc.userlist.util.MockUserDao
import es.manuelrc.userlist.util.launchFragmentInHiltContainer
import es.manuelrc.userlist.viewmodels.UserDetailViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

@HiltAndroidTest
class UserDetailFragmentTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Module
    @InstallIn(SingletonComponent::class)
    object TestAppModule {

        @Singleton
        @Provides
        fun provideUserDetailViewModel(): UserDetailViewModel {
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
            val localUsers = listOf(
                UserEntity(
                    "m", "user2", "surname2", "email2",
                    Picture("", "", ""), "phone2", Location(
                        Street(12, "Plaza castilla"),
                        "Madrid", "Spain", Coordinates("", "")
                    ), Dob("", 0L), false
                )
            )
            val userDao = MockUserDao(localUsers.toMutableList())
            val userApiClient = MockUserApiClient(200, UsersResponse(apiUser))
            val userRepository = DefaultUserRepository(
                DefaultUserRemoteDataSource(userApiClient),
                DefaultUserLocalDataSource(userDao)
            )
            val userDetailInteractor = UserDetailsInteractor(userRepository)
            return UserDetailViewModel(userDetailInteractor)
        }
    }

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testViewWithData() {

        val bundle = UserDetailFragmentArgs("email").toBundle()
        launchFragmentInHiltContainer<UserDetailFragment>(bundle, R.style.Theme_UserList)
        Espresso.onView(withId(R.id.tvName2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvName2))
            .check(ViewAssertions.matches(ViewMatchers.withText("user2 surname2")))
        Espresso.onView(withId(R.id.tvEmail2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvEmail2))
            .check(ViewAssertions.matches(ViewMatchers.withText("email2")))
        Espresso.onView(withId(R.id.tvGender2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvGender2))
            .check(ViewAssertions.matches(ViewMatchers.withText("m")))
        Espresso.onView(withId(R.id.tvLocation2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvLocation2))
            .check(ViewAssertions.matches(ViewMatchers.withText("Plaza castilla 12, Madrid, Spain")))

    }
}