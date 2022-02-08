package com.likefirst.btos.utils

import com.likefirst.btos.data.entities.*
import com.likefirst.btos.data.remote.BaseResponse
import com.likefirst.btos.data.remote.HistoryBaseResponse
import com.likefirst.btos.data.remote.HistorySenderDetailResponse
import com.likefirst.btos.data.remote.plant.response.PlantRequest
import com.likefirst.btos.data.remote.plant.response.PlantResponse
import com.likefirst.btos.data.remote.posting.response.LetterResponse
import com.likefirst.btos.data.remote.response.DiaryResponse
import com.likefirst.btos.data.remote.response.MailboxResponse
import com.likefirst.btos.data.remote.response.ReplyResponse
import com.likefirst.btos.data.remote.users.response.GetProfileResponse
import com.likefirst.btos.data.remote.users.response.LoginResponse
import retrofit2.Call
import retrofit2.http.*

interface RetrofitInterface {

    // ------------------- UserAuth -------------------------- //
    @POST("/auth/google")
    fun login(@Body email: String) : Call<LoginResponse>

    @GET("/auth/jwt")
    fun autoLogin() : Call<LoginResponse>
    //@Header("x-access-token") jwt: String
    @POST("/users")
    fun signUp(@Body user: UserSign) : Call<LoginResponse>

    @GET("/users/{useridx}")
    fun getProfile(@Path("useridx") useridx: Int): Call<GetProfileResponse>

    @PATCH("/users/{userIdx}/sad")
    fun updateIsSad(
        @Path("userIdx") userIdx: Int,
        @Body isSad : UserIsSad
    ) : Call<BaseResponse<String>>


    // -------------------Mailbox -------------------------- //
    @GET("/mailboxes/{userId}")
    fun loadMailbox(
        @Path("userId") id: String
    ): Call<MailboxResponse>

    @GET("/mailboxes/mail")
    fun loadDiary(
        @Query("type") type: String,
        @Query("idx")idx: String
    ): Call<DiaryResponse>

    @GET("/mailboxes/mail")
    fun loadLetter(
        @Query("type") type: String,
        @Query("idx")idx: String
    ): Call<LetterResponse>


    @GET("/mailboxes/mail")
    fun loadReply(
        @Query("type") type: String,
        @Query("idx")idx: String
    ): Call<ReplyResponse>

    // -------------------PlantList-------------------------- //

    @GET("/plants/{userId}")
    fun loadPlantList(
        @Path("userId") id: String
    ): Call<PlantResponse>

    @PATCH("/plants/select")
    fun selectPlant(
        @Body PlantSelectRequest : PlantRequest
    ): Call<PlantResponse>


    @POST("/plants/buy")
    fun buyPlant(
        @Body PlantBuyRequest : PlantRequest
    ): Call<PlantResponse>


    @POST("/plants/{userId}/initialize")
    fun initPlant(
        @Path ("userId") userIdx : String
    ): Call<PlantResponse>

    // ------------------- SettingUser -------------------------- //
    @PATCH("/users/{userIdx}/nickname")
    fun setName(
        @Path("userIdx") userIdx : Int,
        @Body nickName : UserName
    ) : Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/birth")
    fun setBirth(
        @Path("userIdx") userIdx : Int,
        @Body birth : UserBirth
    ): Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/receive/others")
    fun setNotificationOther(
        @Path("userIdx") userIdx : Int,
        @Body recOthers : UserOther
    ): Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/receive/age")
    fun setNotificationAge(
        @Path("userIdx") userIdx : Int,
        @Body recSimilarAge : UserAge
    ): Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/push-alarm")
    fun setPushAlarm(
        @Path("userIdx") userIdx : Int,
        @Body pushAlarm : UserPush
    ): Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/font")
    fun setFont(
        @Path("userIdx") userIdx : Int,
        @Body fontIdx : UserFont
    ): Call<BaseResponse<String>>

    @PATCH("/users/{userIdx}/status")
    fun leave(
        @Path("userIdx") userIdx : Int,
        @Body status : UserLeave
    ) : Call<BaseResponse<String>>

    // ------------------- History -------------------------- //
    @GET("/histories/list/{userIdx}/{pageNum}")
    fun historyListSender(
        @Path("userIdx") userIdx : Int,
        @Path("pageNum") pageNum : Int,
        @Query("filtering") filtering: String,
        @Query("search") search: String?
    ) : Call<HistoryBaseResponse<BasicHistory<SenderList>>>

    @GET("/histories/list/{userIdx}/{pageNum}")
    fun historyListDiaryLetter(
        @Path("userIdx") userIdx : Int,
        @Path("pageNum") pageNum : Int,
        @Query("filtering") filtering : String,
        @Query("search") search : String?
    ) : Call<HistoryBaseResponse<BasicHistory<Content>>>

    @GET("/histories/sender/{userIdx}/{senderNickName}/{pageNum}")
    fun historyListSenderDetail(
        @Path("userIdx") userIdx : Int,
        @Path("senderNickName") senderNickName : String,
        @Path("pageNum") pageNum : Int,
        @Query("search") search : String?
    ) : Call<HistorySenderDetailResponse<Content>>

    @GET("/histories{userIdx}/{type}/{typeIdx}")
    fun historyDetailList(
        @Path("userIdx") userIdx : Int,
        @Path("type") type : String,
        @Path("typeIdx") typeIdx : Int,
    ) : Call<HistorySenderDetailResponse<HistoryList>>
}