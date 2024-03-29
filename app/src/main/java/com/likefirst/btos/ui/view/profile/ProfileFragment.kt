package com.likefirst.btos.ui.view.profile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.likefirst.btos.R
import com.likefirst.btos.data.local.UserDatabase
import com.likefirst.btos.databinding.FragmentProfileBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.view.main.MainActivity
import com.likefirst.btos.ui.view.profile.plant.PlantFragment
import com.likefirst.btos.ui.view.profile.premium.PremiumFragment
import com.likefirst.btos.ui.view.profile.setting.NoticeActivity
import com.likefirst.btos.ui.view.profile.setting.SettingFragment
import com.likefirst.btos.ui.view.profile.setting.SuggestionFragment
import com.likefirst.btos.utils.Model.LiveSharedPreferences
import com.likefirst.btos.ui.viewModel.PlantInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment:BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate){
    var isSettingOpen = false
    var isFetch=true

    val plantViewModel by viewModels<PlantInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val spf = requireActivity().getSharedPreferences("BTOS-APP",MODE_PRIVATE)
        val liveSharedPreference = LiveSharedPreferences(spf)
        liveSharedPreference
            .getString("UserName", "undefine")
            .observe(this, Observer<String> { result ->
                if(result!="undefine"){
                    binding.profileNicknameTv.text=result
                }
            })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val plantName=requireContext()!!.resources.getStringArray(R.array.plantEng)!!
        plantViewModel.getCurrentPlantInfo().observe(viewLifecycleOwner,Observer{
           it-> run {
                binding.profileIv.setImageResource(requireContext()!!.resources.getIdentifier(
                    plantName[it.plantIdx-1]
                            +"_"+it.currentLevel.toString()
                            +"_circle","drawable",
                    requireActivity().packageName))
                binding.profileLevelTv.text=it.plantName+" "+it.currentLevel.toString()+"단계"
                Log.e("plant_changed_profile",it.toString())
             }
        })
        initAfterBinding()
    }


    override fun initAfterBinding() {
        initProfile()
        initClickListener()
    }

    fun initClickListener(){
        val mActivity = activity as MainActivity
        binding.profileCd.setOnClickListener {
          mActivity.supportFragmentManager.beginTransaction()
                .add(R.id.fr_layout,  PlantFragment(), "plantrv")
                .addToBackStack("profile-save")
                .commit()
            //TODO 배포 클릭 방지
        }

        binding.profilePremiumTv.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.fr_layout, PremiumFragment(),"premium")
                .addToBackStack("profile-save")
                .commit()
        }
        binding.profileSettingTv.setOnClickListener {
            isSettingOpen=true
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.fr_layout, SettingFragment(),"setting")
                .show(SettingFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.profileNoticeTv.setOnClickListener {
            val intent = Intent(requireActivity(),NoticeActivity::class.java)
            startActivity(intent)
        }
        binding.profileSuggestTv.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.fr_layout, SuggestionFragment(),"suggestion")
                .addToBackStack("profile-save")
                .commit()
        }
    }

    fun initProfile(){
        val userDatabase = UserDatabase.getInstance(requireContext())!!
        val plant = plantViewModel.getSelectedPlant()
        val plantName=requireContext()!!.resources.getStringArray(R.array.plantEng)!!
        val profile = requireContext()!!.resources.getIdentifier(
            plantName[plant.plantIdx-1]
                    +"_"+plant.currentLevel.toString()
                    +"_circle","drawable",
            requireActivity().packageName)
        binding.profileIv.setImageResource(profile)
        binding.profileNicknameTv.text=userDatabase.userDao().getNickName()!!
        binding.profileLevelTv.text=plant.plantName+" "+plant.currentLevel+"단계"
        if(plant.maxLevel!=plant.currentLevel)binding.profileMaxTv.visibility= View.GONE
    }


    fun cleanUpFragment(  fragments: Array<String>){
        fragments.forEach { fragment ->
            requireActivity().supportFragmentManager.commit {
                requireActivity().supportFragmentManager
                    .findFragmentByTag(fragment)?.let { remove(it) }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (isHidden && isAdded) {
            val fragments = arrayOf("plantItem","suggestion","plantrv")
            cleanUpFragment(fragments)
            if (isSettingOpen) { val fragments = arrayOf("setName", "setBirth", "setAppinfo","setNotify","setAppDetail")
                cleanUpFragment(fragments)
                isSettingOpen=false
            }
            requireActivity().supportFragmentManager.commit {
                requireActivity().supportFragmentManager
                    .findFragmentByTag("setting")?.let { remove(it) }
            }
        }
    }

    fun updateProfile() {
        initProfile()
    }



}