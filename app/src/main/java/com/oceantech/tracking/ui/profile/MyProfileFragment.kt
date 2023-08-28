package com.oceantech.tracking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.data.model.User
import com.oceantech.tracking.databinding.FragmentMyProfileBinding
import com.oceantech.tracking.ui.home.HomeViewAction
import com.oceantech.tracking.ui.home.HomeViewEvent
import com.oceantech.tracking.ui.home.HomeViewModel

class MyProfileFragment : TrackingBaseFragment<FragmentMyProfileBinding>() {
    private val viewmodel:HomeViewModel by activityViewModel()
    private var mUser: User?=null


    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMyProfileBinding {
        return FragmentMyProfileBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel.handle(HomeViewAction.GetCurrentUser)
        views.editSubmit.setOnClickListener {
            editProfileSubmit()
        }

    }

    private fun initUi(user: User) {
        views.apply {
            displayName.text = user.displayName
            lastName.setText(user.lastName)
            firstName.setText(user.firstName)
            gender.setText(user.gender)
            birthPlace.setText(user.birthPlace)
            university.setText(user.university)
            txtYear.setText(user.year.toString())
        }
    }

    private fun editProfileSubmit(){
        val displayName = views.displayName.text.toString()
        val lastName = views.lastName.text.toString()
        val firstName = views.firstName.text.toString()
        val gender = views.gender.text.toString()
        val birthPlace = views.birthPlace.text.toString()
        val university=views.university.text.toString()
        val year=views.txtYear.text.toString()

        if(checkEmpty(lastName, firstName, birthPlace, university, year)){
            setErrorsIfEmpty(lastName, firstName, birthPlace, university, year)
        } else if (!isNumeric(year)) {
            views.txtYearTil.error=getString(R.string.year_is_not_string)
        } else {
            mUser.apply {
                this?.displayName=displayName
                this?.lastName=lastName
                this?.firstName=firstName
                this?.gender=gender
                this?.birthPlace=birthPlace
                this?.university=university
                this?.year=year.toInt()
            }
            viewmodel.handle(HomeViewAction.UpdateMyself(mUser!!))
        }
    }

    private fun isNumeric(input: String): Boolean {
        return input.toIntOrNull() != null
    }

    private fun setErrorsIfEmpty(lastName:String,firstName:String,birthPlace:String,university:String,year:String){
        if(lastName.isNullOrEmpty()) views.lastNameTil.error=getString(R.string.username_not_empty)
        if(firstName.isNullOrEmpty()) views.firstNameTil.error=getString(R.string.username_not_empty)
        if(birthPlace.isNullOrEmpty()) views.birthPlace.error=getString(R.string.username_not_empty)
        if(university.isNullOrEmpty()) views.universityTil.error=getString(R.string.username_not_empty)
        if(year.isNullOrEmpty()) views.txtYearTil.error=getString(R.string.username_not_empty)

    }

    private fun checkEmpty(lastName:String,firstName:String,birthPlace:String,university:String,year:String):Boolean{
        return firstName.isEmpty() ||
                lastName.isEmpty() ||
                birthPlace.isEmpty() ||
                university.isEmpty() ||
                year.isEmpty()
    }

    override fun invalidate(): Unit = withState(viewmodel) {
        when(it.userCurrent){
            is Success ->{
                it.userCurrent.invoke().let { user ->
                    mUser=user
                    initUi(mUser!!)
                }
            }
        }

    }


}