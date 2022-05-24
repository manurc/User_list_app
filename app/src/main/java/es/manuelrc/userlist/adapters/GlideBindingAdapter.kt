package es.manuelrc.userlist.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

object GlideBindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadGlideImage(view: ImageView?, url: String?) {
        if (view != null && url != null && url.isNotEmpty()) {
            view.load(url)
        }
    }
}