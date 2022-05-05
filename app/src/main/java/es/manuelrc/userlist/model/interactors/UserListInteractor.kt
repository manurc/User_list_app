package es.manuelrc.userlist.model.interactors

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.google.android.gms.common.server.response.SafeParcelResponse.from
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.FilterConstrains
import es.manuelrc.userlist.data.source.UserRepository
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.view.utils.DistanceUtil
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.Date.from
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty

class UserListInteractor @Inject constructor(private val userRepository: UserRepository) {


    private val _filter = FilterConstrains(FilterConstrains.OrderedEnum.NAME)

    private val filter = VariableObservable(_filter)

    var observeUsers: Observable<Result.Success<List<UserEntity>>> =
        filter.observable.switchMap { filter ->
            userRepository.observeUsers().map { resultList ->
                applyFilter(resultList.data, filter)
            }
        }


    suspend fun loadUsers() {
        if ((_filter.isFavorite == false || _filter.isFavorite == null) &&
            (_filter.isLocation == false || _filter.isLocation == null)
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
        _filter.apply {
            if (order != null) this.order = order
            if (isFavorite != null) this.isFavorite = isFavorite
            if (isLocation != null) this.isLocation = isLocation
            if (location != null) this.currentLocation = location
            if (query != null) this.query = query
            filter.value = this
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