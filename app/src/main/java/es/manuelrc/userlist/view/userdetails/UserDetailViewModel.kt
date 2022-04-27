package es.manuelrc.userlist.view.userdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.manuelrc.userlist.R
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserDetailsInteractor
import es.manuelrc.userlist.view.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(private val interactor: UserDetailsInteractor) :
    ViewModel() {


    private val _snackbarText = MutableStateFlow(Event(0))
    val snackbarMessage: StateFlow<Event<Int>> = _snackbarText
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private var _userSelected = MutableLiveData<UserEntity>()
    val userSelected: LiveData<UserEntity> get() = _userSelected

    suspend fun findUser(userIdentifier: String) {
        _isLoading.value = true
        val user= interactor.findUser(userIdentifier)
        if(user is Result.Success){
             user.data.let {
                 _userSelected.value = it
            }
        } else if(user is Result.Error){
            _snackbarText.value = Event(R.string.error_obtaining_from_db)
        }
        _isLoading.value = false
    }
}