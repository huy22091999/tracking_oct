package com.oceantech.tracking.ui.users

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.*
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.core.TrackingClickItem
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.DialogSearchBinding
import com.oceantech.tracking.databinding.FragmentUsersBinding
import com.oceantech.tracking.ui.profile.InfoActivity
import com.oceantech.tracking.utils.hideKeyboard
import com.oceantech.tracking.utils.startActivityAnim
import timber.log.Timber
import javax.inject.Inject

class UsersFragment @Inject constructor() : TrackingBaseFragment<FragmentUsersBinding>() {

    private val userViewModel: UserViewModel by activityViewModel()

    lateinit var adapter : UserAdapter
    lateinit var searchAdapter : SearchAdapter

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentUsersBinding {
        return FragmentUsersBinding.inflate(inflater, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRCV()

        listennerUiClick()
    }
    
    private fun setUpRCV() {
        adapter = UserAdapter ( object : TrackingClickItem(){
            override fun onItemUserClickListenner(userId: String) {
                super.onItemUserClickListenner(userId)
                val intent: Intent = Intent(requireContext(), InfoActivity::class.java)
                val bundle: Bundle = Bundle()
                bundle.putString("userID", userId)
                intent.putExtras(bundle)
                requireActivity().startActivityAnim(intent)
            }

            override fun onItemSearchUserClickListenner() {
                super.onItemSearchUserClickListenner()
                showDialogSearch()
            }
        })
        val linearLayoutManager = LinearLayoutManager(requireContext())

        views.rcvUsers.setHasFixedSize(true)
        views.rcvUsers.layoutManager = linearLayoutManager
        views.rcvUsers.adapter = adapter
        views.rcvUsers.addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
    }
    private fun setUpRcvSearch(recyclerView: RecyclerView) {
        searchAdapter = SearchAdapter ( object : TrackingClickItem(){
            override fun onItemUserClickListenner(userId: String) {
                super.onItemUserClickListenner(userId)
                val intent: Intent = Intent(requireContext(), InfoActivity::class.java)
                val bundle: Bundle = Bundle()
                bundle.putString("userID", userId)
                intent.putExtras(bundle)
                requireActivity().startActivityAnim(intent)
            }
        })
        val linearLayoutManager = LinearLayoutManager(requireContext())

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = searchAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation))
    }

    private fun listennerUiClick() {
        views.swipeLayout.setOnRefreshListener {
            userViewModel.handle(UsersViewAction.RefeshUserAction(lifecycleScope))
        }
    }

    override fun invalidate() = withState(userViewModel) {
        when (it.pageUsers) {
            is Success ->{
                adapter.submitData(lifecycle, it.pageUsers.invoke())
                views.swipeLayout.isRefreshing = false
            }

            is Fail ->{
                Timber.e("UsersFragment invalidate Fail:")
            }
        }

        views.progressLoading.isVisible = it.isLoadding()
    }

    private fun showDialogSearch() {
        val dialog = Dialog(requireContext(), com.oceantech.tracking.R.style.StyleDialog_Full_screen_animation)
        val binding = DialogSearchBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.show()

        binding.imgBack.setOnClickListener{
            dialog.dismiss()
        }

        binding.tilSearch.setEndIconOnClickListener{
            binding.edtSearch.setText("")
        }

        binding.tvSearch.setOnClickListener{
            requireContext().hideKeyboard(binding.root)
        }

        setUpRcvSearch(binding.rcv)

        binding.tilSearch.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchAdapter.removeUsers()
                var listUserTemp = ArrayList<User>()
                for (i in 0 until adapter.itemCount){
                    var user: User? = adapter.getUserWithCount(i)
                    var strSearch = s.toString().lowercase()
                    if (user?.displayName != null && strSearch.isNotEmpty() && user.displayName!!.lowercase().contains(strSearch)){
                        listUserTemp.add(user)
                    }
                }
                binding.tvNoData.isVisible = listUserTemp.size <= 0
                searchAdapter.addUser(listUserTemp)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }



}