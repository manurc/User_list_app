package es.manuelrc.userlist.viewmodels

import androidx.lifecycle.ViewModel
import es.manuelrc.userlist.R
import es.manuelrc.userlist.data.Result
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.model.interactors.UserDetailsInteractor
import es.manuelrc.userlist.view.utils.Event
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDetailViewModel @Inject constructor(private val interactor: UserDetailsInteractor) :
    ViewModel() {

    private val _snackbarText = BehaviorSubject.createDefault(Event(0))
    val snackbarMessage: BehaviorSubject<Event<Int>> = _snackbarText
    private val _isLoading = BehaviorSubject.createDefault(false)
    val isLoading: BehaviorSubject<Boolean> get() = _isLoading
    var userSelected = BehaviorSubject.create<UserEntity>()

    suspend fun findUser(userIdentifier: String) = withContext(Dispatchers.IO) {
        _isLoading.onNext(true)
        val user = interactor.findUser(userIdentifier)
        if (user is Result.Success) {
            user.data.let {
                userSelected.onNext(it)
            }
        } else if (user is Result.Error) {
            _snackbarText.onNext(Event(R.string.error_obtaining_from_db))
        }
        _isLoading.onNext(false)
    }
}