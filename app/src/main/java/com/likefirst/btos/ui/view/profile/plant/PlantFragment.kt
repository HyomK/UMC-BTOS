package com.likefirst.btos.ui.view.profile.plant

import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.likefirst.btos.R
import com.likefirst.btos.data.entities.Plant
import com.likefirst.btos.data.remote.plant.PlantRequest
import com.likefirst.btos.databinding.FragmentFlowerpotBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.viewModel.PlantInfoViewModel
import com.likefirst.btos.domain.ApiResult
import com.likefirst.btos.ui.view.main.MainActivity
import com.likefirst.btos.ui.view.profile.plant.PlantDialog
import com.likefirst.btos.ui.view.profile.plant.PlantItemFragment
import com.likefirst.btos.ui.view.profile.plant.PlantRVAdapter
import com.likefirst.btos.utils.NetworkConnection
import com.likefirst.btos.utils.getUserIdx
import com.likefirst.btos.utils.toArrayList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.Comparator
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

@AndroidEntryPoint
class PlantFragment :BaseFragment<FragmentFlowerpotBinding>(FragmentFlowerpotBinding:: inflate), MainActivity.onBackPressedListener
  {

    lateinit var plantName :Array<String>
    lateinit var plantAdapter : PlantRVAdapter
    lateinit var networkConnect : NetworkConnection
    val plantViewModel by viewModels<PlantInfoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        networkConnect = NetworkConnection(requireContext())
        networkConnect.observe(viewLifecycleOwner) { isConnected ->
            run {
                if (isConnected == "false" || isConnected == "null") {
                    Log.e("network-msg",isConnected)
                     GlobalScope.launch {
                        Snackbar.make(binding.root, "인터넷 연결을 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                       //TODO 네트워크 장애 처리
                    }
                }
            }
        }

        plantName=requireContext()!!.resources.getStringArray(R.array.plantEng)!!
        initRecyclerView()
        observePlantItem()
        initAfterBinding()
    }

    fun observePlantItem(){
        plantViewModel.getPlantList().observe(viewLifecycleOwner, Observer { it ->
            it?.let{
                Log.d("HILT_TEST",it.toString())
                plantAdapter.initData(getPlantProfile(loadData(it)))
                plantAdapter.notifyDataSetChanged()
            }
        })

        plantViewModel.res.observe(viewLifecycleOwner, Observer {
            when(it.status){
                 ApiResult.Status.SUCCESS ->{
                     Log.d("plant-api",it.data.toString())
                     binding.setPlantLoadingPb.visibility= View.GONE
                     binding.flowerpotRv.isClickable=true
                 }
                ApiResult.Status.LOADING->{
                    setLoadingView()
                }
                ApiResult.Status.API_ERROR->{
                    Log.d("plant-api",it.message.toString())
                }
            }
        })
    }




    override fun initAfterBinding() {
        val mActivity= activity as MainActivity
        initRecyclerView()
        binding.flowerpotToolbar.toolbarBackIc.setOnClickListener{
            mActivity.supportFragmentManager.popBackStack()
        }
        plantAdapter!!?.setMyItemCLickLister(object:PlantRVAdapter.PlantItemClickListener{
            override fun onClickInfoItem(plant: Plant){
                val plantItemFragment = PlantItemFragment()
                val bundle = Bundle()
                bundle.putParcelable("plantItem",plant)
                plantItemFragment.arguments=bundle
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fr_layout,plantItemFragment,"plantItem").addToBackStack(null)
                    .commit()

            }

            @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
            override fun onClickSelectItem(plant : Plant,position:Int) {
                Log.e("network",networkConnect.value.toString() )
                if(  networkConnect.value == "true"){
                    CoroutineScope(Dispatchers.Main).launch {
                        setLoadingView()
                        binding.flowerpotRv.isClickable = false
                        plantViewModel.selectPlantItem(PlantRequest(getUserIdx(), plant.plantIdx))
                    }
                }else{
                    Snackbar.make(binding.root,
                        "인터넷 연결을 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                }

            }


            override fun onClickBuyItem(plant : Plant) {
                val buyDialog = PlantDialog()
                var checking = true
                val btn =arrayOf("취소","구매")
                val bundle = bundleOf(
                    "bodyContext" to "${plant.plantName} 화분을 구매하시겠습니까?",
                    "btnData" to btn
                )
                buyDialog.arguments=bundle
                buyDialog.setButtonClickListener(object: PlantDialog.OnButtonClickListener{
                    override fun onButton1Clicked() { checking = false}

                    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
                    override fun onButton2Clicked(){
                        if( networkConnect.value == "true"){
                            runBlocking {
                                val job =  plantViewModel.buyPlantItem(PlantRequest(getUserIdx(),plant.plantIdx))
                                job.join()
                            }
                            buyDialog.dismiss()

                        }else{
                            buyDialog.dismiss()
                            Snackbar.make(binding.root,
                                "인터넷 연결을 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                        }

                    }
                })
                buyDialog.show(requireActivity().supportFragmentManager, "PlantDialog")
            }
        })

    }

    fun  loadData( list : List<Plant>) : ArrayList<Plant> {
        val sortedList  =list.sortedWith(ComparePlant)
        return sortedList.toArrayList()
    }

    fun initRecyclerView(){
        plantAdapter= PlantRVAdapter(requireContext())
        binding.flowerpotRv.adapter=plantAdapter
    }

    fun getPlantProfile(plantList:ArrayList<Plant>):ArrayList<Pair<Plant,Int>>{
        val plantName=requireContext()!!.resources.getStringArray(R.array.plantEng)!!
        val activity = activity as MainActivity
        val ImageList = plantList.map{plant: Plant -> Pair<Plant, Int>(
            plant,
            if(plant.isOwn){
                requireContext()!!.resources.getIdentifier(
                    plantName[plant.plantIdx-1]
                            +"_"+plant.currentLevel.toString()
                            +"_circle","drawable",
                    activity.packageName)
            }else{
                requireContext()!!.resources.getIdentifier(
                    plantName[plant.plantIdx-1]
                            +"_"+plant.maxLevel.toString()
                            +"_circle","drawable",
                    activity.packageName)
            })

        }
        return ImageList.toArrayList()
    }


    override fun onBackPressed() {
        val mainActivity = activity as MainActivity
        if(mainActivity.isPlantOpen){
            mainActivity.onBottomNavHandler("plant_notice",R.id.homeFragment)
            mainActivity.supportFragmentManager.beginTransaction().remove(this).commit()
            return
        }
        mainActivity.supportFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        val mainActivity = activity as MainActivity
        mainActivity.isPlantOpen=false
    }

    fun setLoadingView(){
        binding.setPlantLoadingPb.visibility= View.VISIBLE
        binding.setPlantLoadingPb.apply {
            setAnimation("sprout_loading.json")
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    class ComparePlant {
        companion object : Comparator<Plant> {
            override fun compare(a: Plant, b: Plant): Int {
                var p1=0
                var p2=0
                when(a.plantStatus){
                    "selected"->p1=2
                    "active"-> p1=2
                    "inactive"-> p1=1
                }
                when(b.plantStatus){
                    "selected"->p2=2
                    "active"-> p2=2
                    "inactive"-> p2=1
                }
                return p2-p1
            }
        }
    }
 }
