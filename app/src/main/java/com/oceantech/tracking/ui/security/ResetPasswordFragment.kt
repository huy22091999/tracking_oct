package com.oceantech.tracking.ui.security


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.oceantech.tracking.R
import com.oceantech.tracking.core.TrackingBaseFragment
import com.oceantech.tracking.databinding.FragmentResetPasswordBinding
import com.oceantech.tracking.ui.MainActivity
import nl.joery.animatedbottombar.AnimatedBottomBar
//done
class ResetPasswordFragment :TrackingBaseFragment<FragmentResetPasswordBinding>() {
    private lateinit var bottomNavigation: AnimatedBottomBar
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentResetPasswordBinding {
        return FragmentResetPasswordBinding.inflate(inflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTurnOff()
        views.btnBack.setOnClickListener {
            findNavController().popBackStack();
        }
    }

    private fun setupTurnOff() {
        if(activity is MainActivity) {
            bottomNavigation = requireActivity().findViewById(R.id.bottomNavigationView)
            bottomNavigation.visibility = View.GONE
            (activity as AppCompatActivity).supportActionBar?.hide()
        }
    }

    override fun onDestroyView() {
        bottomNavigation.visibility= View.VISIBLE
        (activity as AppCompatActivity).supportActionBar?.show()
        super.onDestroyView()
    }

}