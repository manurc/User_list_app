package es.manuelrc.userlist.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.manuelrc.userlist.model.UserEntity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

object GlideBindingAdapter {


    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadGlideImage(view: ImageView?, user: BehaviorSubject<UserEntity?>) {

        if (view != null) {
            user
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<UserEntity>() {
                    override fun onNext(user: UserEntity) {
                        Glide.with(view.context)
                            .load(user.picture.large)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(view)
                    }

                    override fun onError(e: Throwable) {
                    }
                    override fun onComplete() {
                    }
                })
        }
    }
}