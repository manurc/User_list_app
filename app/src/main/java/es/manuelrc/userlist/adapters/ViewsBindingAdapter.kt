package es.manuelrc.userlist.adapters

import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

object ViewsBindingAdapter {

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setVisibility(view: View, status: Boolean) {
        view.isVisible = status
    }
}