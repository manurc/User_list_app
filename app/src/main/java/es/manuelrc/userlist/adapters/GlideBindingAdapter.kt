package es.manuelrc.userlist.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object GlideBindingAdapter {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadGlideImage(view: ImageView?, url: String?) {
        if (view != null && url != null && url.isNotEmpty()) {
            Glide.with(view.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(view)
        }
    }
}