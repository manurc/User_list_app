package es.manuelrc.userlist.adapters

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.view.utils.UserDetailsEnum
import es.manuelrc.userlist.view.utils.UserPrinter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

object ViewsBindingAdapter {

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setVisibility(view: View, status: BehaviorSubject<Boolean>) {
        status
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<Boolean>() {
                override fun onNext(visibility: Boolean) {
                    view.isVisible = visibility
                }

                override fun onError(e: Throwable) {
                }
                override fun onComplete() {
                }
            })

    }

    @JvmStatic
    @BindingAdapter("rxText", "rxEnum")
    fun rxText(editText: TextView, user: BehaviorSubject<UserEntity>, enum: UserDetailsEnum) {

        var msg = ""
        user
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<UserEntity>() {
                override fun onNext(user: UserEntity) {
                    user.let {
                        msg = when(enum){
                            UserDetailsEnum.GENDER -> it.gender
                            UserDetailsEnum.NAME -> UserPrinter.printUserFullName(it)
                            UserDetailsEnum.LOCATION -> it.location.toString()
                            UserDetailsEnum.DATE -> it.registered.date
                            UserDetailsEnum.EMAIL -> it.email
                        }
                    }
                    editText.text = msg
                }

                override fun onError(e: Throwable) {
                }
                override fun onComplete() {
                }
            })
        }
}