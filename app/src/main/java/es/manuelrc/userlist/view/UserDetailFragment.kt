package es.manuelrc.userlist.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.manuelrc.userlist.R
import es.manuelrc.userlist.databinding.FragmentUserDetailsBinding
import es.manuelrc.userlist.viewmodels.UserDetailViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserDetailFragment : Fragment() {

    @Inject
    lateinit var userDetailViewModel: UserDetailViewModel

    private lateinit var mBinding: FragmentUserDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.title = mainActivity.getString(R.string.users_description)
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_details,
            container, false
        )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.userDetailViewModel = userDetailViewModel
        setupViewModel()
    }

    private fun setupViewModel() {

        arguments?.let {
            val email = it.getString("UserIdentifier")
            lifecycleScope.launch {
                userDetailViewModel.findUser(email ?: "")
            }
        }
        lifecycleScope.launch {
            userDetailViewModel.snackbarMessage.collect { event ->
                val msg = event.getContentIfNotHandled()
                if (msg != null && event.peekContent() != 0) {
                    view?.let {
                        Snackbar.make(mBinding.root, getString(msg), Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    }
}