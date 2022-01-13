package com.likefirst.btos.ui.main


import android.media.Image
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.likefirst.btos.R

import com.likefirst.btos.databinding.FragmentHomeBinding
import com.likefirst.btos.ui.BaseFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


public class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {


    override fun initAfterBinding() {
        val animationView: LottieAnimationView = binding.lottieAnimation
        animationView.loop(true)
        animationView.playAnimation()

        val mActivity = activity as MainActivity


        binding.homeNotificationBtn.setOnClickListener {
            mActivity.findViewById<DrawerLayout>(R.id.main_layout).openDrawer((GravityCompat.START))

        }

        binding.homeMailBtn.setOnClickListener {
            mActivity.changeFragment().moveFragment(R.id.home_mailbox_layout,MailboxFragment())
        }

        binding.homeWriteBtn.setOnClickListener {

        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        setWindowImage()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWindowImage(){
        val current : LocalDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("h")
        val formatted = current.format(formatter)
        val now = formatted.toInt()


        if(now<=5){
            binding.windowIv.setImageResource(R.mipmap.window_morning)
        }else if(now in 12..18) {
            binding.windowIv.setImageResource(R.mipmap.window_afternoon)
        }else{
            binding.windowIv.setImageResource(R.mipmap.window_night)
        }
    }

}