package com.likefirst.btos.data.remote.plant.service


import android.util.Log
import com.likefirst.btos.ApplicationClass
import com.likefirst.btos.data.remote.plant.response.PlantRequest
import com.likefirst.btos.data.remote.plant.response.PlantResponse
import com.likefirst.btos.data.remote.plant.view.PlantBuyView
import com.likefirst.btos.data.remote.plant.view.PlantInitView
import com.likefirst.btos.data.remote.plant.view.PlantListView
import com.likefirst.btos.data.remote.plant.view.PlantSelectView
import com.likefirst.btos.utils.RetrofitInterface
import com.likefirst.btos.utils.errorDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlantService {

    private lateinit var plantView : PlantListView
    private lateinit var plantSelectView: PlantSelectView
    private lateinit var plantBuyView: PlantBuyView
    private lateinit var plantInitView: PlantInitView

    private val PlantService = ApplicationClass.retrofit.create(RetrofitInterface::class.java)


    fun setPlantListView(plantView: PlantListView){
        this.plantView=plantView
    }

    fun setPlantSelectView(plantSelectView: PlantSelectView){
        this.plantSelectView=plantSelectView
    }

    fun setPlantBuyView(plantBuyView: PlantBuyView){
        this.plantBuyView=plantBuyView
    }

    fun setPlantInitView(plantInitView: PlantInitView){
        this.plantInitView=plantInitView
    }

    fun loadPlantList(userId:String){
        plantView.onPlantListLoading()

        PlantService.loadPlantList(userId).enqueue(object:Callback<PlantResponse>{
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                val plantResponse: PlantResponse =response.body()!!
                Log.e("PlantListLoad/API", plantResponse.toString())

                when(plantResponse.code){
                    1000->plantView.onPlantListSuccess( plantResponse.result!!)
                    else->plantView.onPlantListFailure( plantResponse.code,plantResponse.message)
                }
            }

            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                plantView.onPlantListFailure( 4000,"데이터베이스 연결에 실패하였습니다.")
            }

        })
    }

    fun selectPlant(request: PlantRequest){
        PlantService.selectPlant(request).enqueue(object:Callback<PlantResponse>{
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                val plantResponse: PlantResponse? = response.body()
                if(plantResponse==null){
                    plantSelectView.onPlantSelectError(errorDialog())
                    return
                }
                Log.e("PlantSELECT/API", plantResponse.toString())

                if(response.isSuccessful){
                    plantSelectView.onPlantSelectSuccess(request.plantIdx,response.body()!!)
                }else{
                    plantSelectView.onPlantSelectFailure( plantResponse.code,plantResponse.message)
                }
            }

            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                plantSelectView.onPlantSelectFailure( 4000,"데이터베이스 연결에 실패하였습니다.")
            }
        })

    }
    fun buyPlant(request: PlantRequest){
        PlantService.buyPlant(request).enqueue(object:Callback<PlantResponse>{
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                val plantResponse =response.body()
                if(plantResponse==null){
                    plantBuyView.onPlantBuyError(errorDialog())
                    return
                }

                if(response.isSuccessful){
                    plantBuyView.onPlantBuySuccess(request.plantIdx,response.body()!!)
                }else{
                    plantBuyView.onPlantBuyFailure( plantResponse.code,plantResponse.message)
                }

                Log.e("PlantBUY/API", plantResponse.toString())

            }


            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                plantBuyView.onPlantBuyFailure( 4000,"데이터베이스 연결에 실패하였습니다.")
            }
        })
    }

    fun initPlant(userId: String){
        PlantService.initPlant(userId).enqueue(object:Callback<PlantResponse>{
            override fun onResponse(call: Call<PlantResponse>, response: Response<PlantResponse>) {
                val plantResponse =response.body()!!

                if(response.isSuccessful){
                    plantInitView.onPlantInitSuccess(1)
                }else{
                    plantInitView.onPlantInitFailure( plantResponse.code,plantResponse.message)
                }
                Log.e("PlantInit/API", plantResponse.toString())

            }


            override fun onFailure(call: Call<PlantResponse>, t: Throwable) {
                plantInitView.onPlantInitFailure( 4000,"데이터베이스 연결에 실패하였습니다.")
                plantInitView.onPlantInitFailure( 7000,"해당 유저의 화분 초기화에 실패하였습니다.")
            }
        })
    }



}

