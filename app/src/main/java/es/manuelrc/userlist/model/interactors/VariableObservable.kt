package es.manuelrc.userlist.model.interactors

import io.reactivex.subjects.BehaviorSubject

class VariableObservable <T : Any>(defaultValue: T) {
    var value: T = defaultValue
        set(value) {
            field = value
            observable.onNext(value)
        }
    val observable = BehaviorSubject.createDefault(value)
}