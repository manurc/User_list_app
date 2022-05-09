package es.manuelrc.userlist.view

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import es.manuelrc.userlist.R
import es.manuelrc.userlist.adapters.OnUserClickListener
import es.manuelrc.userlist.adapters.UserListAdapter
import es.manuelrc.userlist.data.source.FilterConstrains
import es.manuelrc.userlist.databinding.FragmentUserListBinding
import es.manuelrc.userlist.model.UserEntity
import es.manuelrc.userlist.viewmodels.SharedViewModel
import es.manuelrc.userlist.viewmodels.UserListViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment : Fragment(), OnUserClickListener {

    @Inject
    lateinit var mSharedViewModel: SharedViewModel

    @Inject
    lateinit var mUserListViewModel: UserListViewModel

    private lateinit var mBinding: FragmentUserListBinding

    private lateinit var mAdapter: UserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.title = mainActivity.getString(R.string.users_list)
        mBinding = FragmentUserListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        mAdapter = UserListAdapter(this)
        val mLinearLayout = LinearLayoutManager(requireActivity())
        mBinding.recyclerview.apply {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = mLinearLayout
            adapter = mAdapter
        }
    }

    private fun setupViewModel() {
        with(mUserListViewModel) {
            lifecycleScope.launch {
                mUsers
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mAdapter.submitList(it.data)
                        if (it.data.isEmpty()) mUserListViewModel.loadUsers()
                    }
            }
            lifecycleScope.launch {
                snackbarMessage
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { event ->
                        val msg = event.getContentIfNotHandled()
                        if (msg != null && event.peekContent() != 0) {
                            view?.let {
                                if (msg == R.string.error_location) mBinding.cbDistance.isChecked =
                                    false
                                Snackbar.make(mBinding.root, getString(msg), Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
            }
            lifecycleScope.launch {
                isLoading
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        mBinding.progressBar.isVisible = it
                    }
            }
            lifecycleScope.launch {
                sortType
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { event ->
                        event.getContentIfNotHandled()?.let { order ->
                            when (order) {
                                FilterConstrains.OrderedEnum.GENDER -> {
                                    mBinding.cbGender.isChecked = true
                                    mBinding.cbName.isChecked = false
                                }
                                FilterConstrains.OrderedEnum.NAME -> {
                                    mBinding.cbGender.isChecked = false
                                    mBinding.cbName.isChecked = true
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun setupView() {
        with(mBinding) {
            fab.setOnClickListener {
                mUserListViewModel.addUser()
            }

            cbFavorite.setOnClickListener {
                mUserListViewModel.filterUsers(isFavorite = cbFavorite.isChecked)
            }

            cbDistance.setOnClickListener {
                mSharedViewModel.askLocation()
                mUserListViewModel.filterUsers(isLocation = cbDistance.isChecked)
            }

            cbGender.setOnClickListener {
                mUserListViewModel.filterUsers(FilterConstrains.OrderedEnum.GENDER)
            }
            cbName.setOnClickListener {
                mUserListViewModel.filterUsers(FilterConstrains.OrderedEnum.NAME)
            }
        }
    }

    override fun onFavoriteUser(user: UserEntity) {
        mUserListViewModel.updateUser(user)
    }

    override fun onDeleteUser(user: UserEntity) {
        mUserListViewModel.deleteUser(user)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val actionSearchMenu = menu.findItem(R.id.action_search)
        val searchView = actionSearchMenu.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    mUserListViewModel.filterUsers(query = query)
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    mUserListViewModel.filterUsers(query = s)
                    return false
                }
            })
            setOnCloseListener {
                mUserListViewModel.filterUsers(query = "")
                false
            }
            queryHint = getString(R.string.search_hint)
        }
    }
}