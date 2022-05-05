package es.manuelrc.userlist.model.interactors

import android.location.Location
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.FilterConstrains
import es.manuelrc.userlist.data.source.UserRepository
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.view.utils.DistanceUtil
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class UserListInteractor @Inject constructor(private val userRepository: UserRepository) {


    private val filter = BehaviorSubject.createDefault(FilterConstrains(FilterConstrains.OrderedEnum.NAME))

    var observeUsers: Observable<Result.Success<List<UserEntity>>> =
        filter.switchMap { filter ->
            userRepository.observeUsers().map { resultList ->
                applyFilter(resultList.data, filter)
            }
        }


    suspend fun loadUsers() {
        if ((filter.value?.isFavorite == false || filter.value?.isFavorite == null) &&
            (filter.value?.isLocation == false || filter.value?.isLocation == null)
        )
            userRepository.addNewUsers(5)
    }

    suspend fun addNewUser(callback: () -> Unit) {
        userRepository.addNewUsers(1)
        callback()
    }

    fun deleteUsers(user: UserEntity) {
        userRepository.deleteUser(user)
    }

    fun updateUsers(user: UserEntity) {
        user.isFavorite = !user.isFavorite
        userRepository.updateUser(user)
    }

    fun updateFilter(
        order: FilterConstrains.OrderedEnum?,
        isFavorite: Boolean?,
        isLocation: Boolean?,
        location: Location?,
        query: String?
    ) {
        filter.apply {
            if (order != null) this.value?.order = order
            if (isFavorite != null) this.value?.isFavorite = isFavorite
            if (isLocation != null) this.value?.isLocation = isLocation
            if (location != null) this.value?.currentLocation = location
            if (query != null) this.value?.query = query
            if(this.value!=null)
            filter.onNext(this.blockingFirst())
        }


    }

    private fun applyFilter(
        userList: List<UserEntity>,
        filter: FilterConstrains
    ): Result.Success<List<UserEntity>> {

        var filteredUserList = userList.sortedBy {
            when (filter.order) {
                FilterConstrains.OrderedEnum.GENDER -> it.gender
                FilterConstrains.OrderedEnum.NAME -> it.name
            }
        }.filter {
            it.name.contains(filter.query) || it.surname.contains(filter.query) || it.email.contains(
                filter.query
            )
        }

        if (filter.isFavorite != null && filter.isFavorite != false) {
            filteredUserList = filteredUserList.filter { user ->
                user.isFavorite == filter.isFavorite
            }
        }
        if (filter.isLocation != null && filter.isLocation != false && filter.currentLocation != null) {
            filteredUserList = filteredUserList.filter { user ->
                DistanceUtil.distanceInKm(
                    filter.currentLocation!!.latitude,
                    filter.currentLocation!!.longitude,
                    user.location.coordinates.latitude.toDouble(),
                    user.location.coordinates.longitude.toDouble()
                ) <= 1000
            }
        }
        return Result.Success(filteredUserList)
    }
}