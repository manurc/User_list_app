package es.manuelrc.userlist.viewmodels


import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.manuelrc.userlist.R
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.data.source.FilterConstrains
import es.manuelrc.userlist.data.source.local.DBException
import es.manuelrc.userlist.data.source.remote.ApiResponseException
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.exceptions.TypeError
import es.manuelrc.userlist.model.interactors.UserListInteractor
import es.manuelrc.userlist.view.utils.Event
import io.reactivex.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserListViewModel @Inject constructor(private val interactor: UserListInteractor) :
    ViewModel() {


    val mUsers: Observable<Result.Success<List<UserEntity>>> = interactor.observeUsers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _sortType = MutableStateFlow(Event(FilterConstrains.OrderedEnum.NAME))
    val sortType: StateFlow<Event<FilterConstrains.OrderedEnum>> get() = _sortType
    private var currentLocation: Location? = null
    private val _snackbarText = MutableStateFlow(Event(0))
    val snackbarMessage: StateFlow<Event<Int>> = _snackbarText

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        CoroutineScope(Dispatchers.Main).launch {
            throwable.printStackTrace()
            var msg = R.string.unknown_error
            if (throwable.cause is ApiResponseException) {
                msg = R.string.error_downloading_users
            }
            _snackbarText.value = Event(msg)
        }
    }

    fun loadUsers() {
        executeAction {
            interactor.loadUsers()
        }
    }

    fun deleteUser(user: UserEntity) {
        executeAction {
            interactor.deleteUsers(user)
        }
    }

    fun updateUser(user: UserEntity) {
        executeAction {
            interactor.updateUsers(user)
        }
    }

    fun addUser() {
        executeAction {
            interactor.addNewUser {
                _snackbarText.value = Event(R.string.user_added)
            }
        }
    }

    fun updateLocation(location: Location) {
        this.currentLocation = location
    }

    fun errorLoading(exception: Exception) {
        var msg = R.string.unknown_error
        if (exception is DBException) {
            msg = when (exception.type) {
                TypeError.GET -> R.string.error_obtaining_from_db
                TypeError.INSERT -> R.string.error_inserting_to_db
                TypeError.UPDATE -> R.string.error_updating_data_to_db
                TypeError.DELETE -> R.string.error_deleting_data
                TypeError.LOCATION_NULL -> R.string.error_location
            }
        }
        _snackbarText.value = Event(msg)
    }

    fun filterUsers(
        order: FilterConstrains.OrderedEnum? = null,
        isFavorite: Boolean? = null,
        isLocation: Boolean? = null,
        query: String? = null,
    ) {
        if (order != null) {
            _sortType.value = Event(order)
        }
        if (currentLocation == null) {
            errorLoading(DBException(TypeError.LOCATION_NULL))
        }
        executeAction {
            interactor.updateFilter(order, isFavorite, isLocation, currentLocation, query)
        }
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                block()
            } finally {
                _isLoading.value = false
            }
        }
    }

}