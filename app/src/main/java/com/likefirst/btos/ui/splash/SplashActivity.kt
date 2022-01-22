package com.likefirst.btos.ui.splash

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.likefirst.btos.databinding.ActivitySplashBinding
import com.likefirst.btos.ui.BaseActivity
import com.likefirst.btos.ui.main.LoginActivity

class SplashActivity : BaseActivity<ActivitySplashBinding> (ActivitySplashBinding::inflate) {
    override fun initAfterBinding() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(intent)
        },5000)
    }


}