package com.oceantech.tracking.ui.security

import android.content.Intent
import android.os.Bundle
import com.oceantech.tracking.core.TrackingBaseActivity
import com.oceantech.tracking.databinding.ActivityAcceptBinding
import com.oceantech.tracking.ui.MainActivity

class AcceptActivity : TrackingBaseActivity<ActivityAcceptBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(views.root)
        views.acceptUse.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    override fun getBinding(): ActivityAcceptBinding {
        return ActivityAcceptBinding.inflate(layoutInflater)
    }

}