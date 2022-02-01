package com.likefirst.btos.ui.profile.plant

import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.likefirst.btos.R
import com.likefirst.btos.databinding.FragmentFlowerpotBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.main.MainActivity
import com.likefirst.btos.data.entities.Plant
import com.likefirst.btos.data.local.PlantDatabase
import com.likefirst.btos.data.remote.response.PlantRequest
import com.likefirst.btos.data.remote.response.PlantResponse
import com.likefirst.btos.data.remote.service.PlantService
import com.likefirst.btos.data.remote.view.plant.PlantBuyView
import com.likefirst.btos.data.remote.view.plant.PlantSelectView
import com.likefirst.btos.ui.main.CustomDialogFragment
import com.likefirst.btos.ui.profile.ProfileFragment
import kotlin.collections.ArrayList

class PlantFragment :BaseFragment<FragmentFlowerpotBinding>(FragmentFlowerpotBinding:: inflate), MainActivity.onBackPressedListener  , PlantSelectView, PlantBuyView{

    interface SelectListener {
        fun isSuccessToSelect(isSuccess : Boolean)
    }

    val USERIDX=2
    private val plantBuyView:PlantBuyView =this
    override fun initAfterBinding() {
        val mActivity= activity as MainActivity
        val adapter = PlantRVAdapter(loadData())
        val plantSelectView :PlantSelectView =this

        val plantService = PlantService()

        binding.flowerpotRv.adapter=adapter

        adapter.setMyItemCLickLister(object:PlantRVAdapter.PlantItemClickListener{
            override fun onClickInfoItem(plant:Plant){
                val plantItemFragment = PlantItemFragment()
                val bundle = Bundle()
                bundle.putParcelable("plantItem",plant)
                plantItemFragment.arguments=bundle
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fr_layout,plantItemFragment,"plantItem").addToBackStack(null)
                    .commit()

            }

            override fun onClickSelectItem(plant : Plant) {
                val plantService = PlantService()
                plantService.setPlantSelectView(plantSelectView)
                val request :PlantRequest = PlantRequest(USERIDX,plant.plantIdx)
                plantService.selectPlant( request )
            }

            override fun onClickBuyItem(plant : Plant) {
                val plantService = PlantService()
                plantService.setPlantBuyView(plantBuyView)
                val request :PlantRequest = PlantRequest(USERIDX,plant.plantIdx)
                plantService.buyPlant(request)

            }
        })

        binding.flowerpotToolbar.toolbarBackIc.setOnClickListener{
            mActivity.supportFragmentManager.popBackStack()
        }

    }


   fun  loadData() : List<Plant> {
        val plantDB = PlantDatabase.getInstance(requireContext()!!)
        val list =plantDB?.plantDao()?.getPlants()!!
        return list
    }




    override fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onPlantBuyError(Dialog: CustomDialogFragment) {
        Dialog.show(requireActivity().supportFragmentManager,"plantError")
    }

    override fun onPlantBuySuccess(plantIdx: Int, response : PlantResponse) {
        Log.d("Plantbuy/API","succes")
        val plantDB = PlantDatabase.getInstance(requireContext()!!)!!
        plantDB.plantDao().setPlantStatus(plantIdx,"active")

    }

    override fun onPlantBuyFailure(code: Int, message: String) {
        Log.d("Plantbuy/API",message.toString())
    }


    override fun onPlantSelectError(Dialog: CustomDialogFragment) {
        Dialog.show(requireActivity().supportFragmentManager,"plantError")

    }

    override fun onPlantSelectSuccess(plantIdx: Int, request: PlantResponse) {
        Log.d("Plantselect/API",request.isSuccess.toString())

        val plantDB = PlantDatabase.getInstance(requireContext()!!)!!
        val selected = plantDB.plantDao().getSelectedPlant("selected")!!
        plantDB.plantDao().setPlantStatus(selected.plantIdx,"active")
        plantDB.plantDao().setPlantStatus(plantIdx,"selected")

    }

    override fun onPlantSelectFailure(code: Int, message: String) {
        TODO("Not yet implemented")
    }


}

