package es.manuelrc.userlist.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import es.manuelrc.userlist.R
import es.manuelrc.userlist.databinding.ItemUserBinding
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.view.UserListFragmentDirections
import es.manuelrc.userlist.view.utils.UserPrinter

class UserListAdapter(private var listener: OnUserClickListener) :
    ListAdapter<UserEntity, RecyclerView.ViewHolder>(UserDiffCallback()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
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
                imgPhoto.load(user.picture.large)
            }
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