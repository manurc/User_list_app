package es.manuelrc.userlist.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import es.manuelrc.userlist.R
import es.manuelrc.userlist.databinding.ItemUserBinding
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.view.UserListFragmentDirections
import es.manuelrc.userlist.view.utils.UserPrinter

class UserListAdapter(private var listener: OnUserClickListener) :
    ListAdapter<UserEntity, RecyclerView.ViewHolder>(UserDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = getItem(position)

        with(holder as ViewHolder) {
            setListener(user)
            binding.apply {
                tvName.text = UserPrinter.printUserFullName(user)
                tvEmail.text = user.email
                tvPhone.text = user.phone
                cbFavorite.isChecked = user.isFavorite
            }
            Glide.with(mContext)
                .load(user.picture.large)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemUserBinding.bind(view)
        fun setListener(user: UserEntity) {
            with(binding) {
                imgPhoto.setOnClickListener {
                    imgPhoto.findNavController().navigate(
                        UserListFragmentDirections.actionUserListFragmentToUserDetailFragment(user.email)
                    )
                }
                cbFavorite.setOnClickListener { listener.onFavoriteUser(user) }
                cbDelete.setOnClickListener { listener.onDeleteUser(user) }
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem.email == newItem.email
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem == newItem
        }
    }
}