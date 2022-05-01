package es.manuelrc.userlist.viewmodels

import androidx.lifecycle.ViewModel
import es.manuelrc.userlist.view.utils.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _locationPermission = MutableStateFlow(Event(false))
    val locationPermission: StateFlow<Event<Boolean>> get() = _locationPermission

    fun askLocation() {
        _locationPermission.value = Event(true)
    }


}