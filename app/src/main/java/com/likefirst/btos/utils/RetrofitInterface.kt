package com.likefirst.btos.utils

import com.likefirst.btos.data.entities.*
import com.likefirst.btos.data.entities.firebase.FcmTokenRequest
import com.likefirst.btos.data.remote.BaseResponse
import com.likefirst.btos.data.remote.history.response.HistoryBaseResponse
import com.likefirst.btos.data.remote.history.response.HistoryDetailResponse
import com.likefirst.btos.data.remote.history.response.HistorySenderDetailResponse
import com.likefirst.btos.data.remote.notify.response.*
import com.likefirst.btos.data.remote.posting.response.*
import com.likefirst.btos.data.remote.users.response.BlackList
import com.likefirst.btos.data.remote.users.response.BlockUser
import com.likefirst.btos.data.remote.users.response.GetProfileResponse
import com.likefirst.btos.data.remote.users.response.LoginResponse
import com.likefirst.btos.data.remote.viewer.response.ArchiveCalendar
import com.likefirst.btos.data.remote.viewer.response.ArchiveDiaryResult
import com.likefirst.btos.data.remote.viewer.response.ArchiveList
import com.likefirst.btos.data.remote.viewer.response.UpdateDiaryRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RetrofitInterface {

    // ------------------- UserAuth -------------------------- //
    @POST("/auth/google")
    fun login(@Body email: UserEmail) : Call<LoginResponse>

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
    @GET("/mailboxes")
    fun loadMailbox(
        @Query("userIdx") id: Int
    ): Call<MailboxResponse>

    @GET("/mailboxes/mail")
    fun loadDiary(
        @Query("userIdx")userIdx: Int,
        @Query("type") type: String = "diary",
        @Query("typeIdx")idx:Int
    ): Call<BaseResponse<MailInfoResponse>>

    @GET("/mailboxes/mail")
    fun loadLetter(
        @Query("userIdx")  userIdx: Int,
        @Query("type") type: String = "letter",
        @Query("typeIdx")idx:Int
    ): Call<BaseResponse<MailInfoResponse>>


    @GET("/mailboxes/mail")
    fun loadReply(
        @Query("userIdx") userIdx: Int,
        @Query("type") type: String = "reply",
        @Query("typeIdx")idx:Int
    ): Call<BaseResponse<MailInfoResponse>>


    @GET("/mailboxes")
    suspend fun getMailbox(
        @Query("userIdx") id: Int
    ): Response<MailboxResponse>



    // -------------------PlantList-------------------------- //

 /*   @GET("/plants/{userId}")
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
*/
    // -------------------Notice-------------------------- //
    @GET("/notices")
    fun loadNotice(): Call<NoticeAPIResponse>



    // ---------------- Archive Calendar ------------------ //
    @GET("/archives/calendar/{userIdx}/{date}")
    fun getCalendar(
        @Path("userIdx") userIdx: Int,
        @Path("date") date : String,
        @Query("type") type : String
    ) : Call<BaseResponse<ArrayList<ArchiveCalendar>>>

    @POST("/diaries")
    fun postDiary(
        @Body postDiaryRequest : PostDiaryRequest
    ) : Call<BaseResponse<PostDiaryResponse>>

    @GET("/archives/{diaryIdx}")
    fun getDiary(
        @Path("diaryIdx") diaryIdx : Int
        ) : Call<BaseResponse<ArchiveDiaryResult>>

    // ---------------- Archive List ----------------- //
    @GET("/archives/diaryList/{userIdx}/{pageNum}")
    fun getArchiveList(
        @Path("userIdx") userIdx : Int,
        @Path("pageNum") pageNum : Int,
        @QueryMap search : Map<String, String>?
    ) : Call<ArchiveList>

    @PUT("/diaries")
    fun updateDiary(
        @Body updateRequest : UpdateDiaryRequest
    ) : Call<BaseResponse<String>>

    @PATCH("/diaries/delete/{diaryIdx}")
    fun deleteDiary(
        @Path("diaryIdx") diaryIdx: Int,
        @Query("userIdx") userIdx : Int
    ) : Call<BaseResponse<String>>
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

    // -------------------blacklist-------------------------- //
    @POST("/blocklists")
    fun setBlock(
        @Body blacklist : BlackList
    ): Call<BaseResponse<Int>>

    @GET("/blocklists")
    fun getBlackList(
        @Query("userIdx") userIdx : Int
    ): Call<BaseResponse<ArrayList<BlockUser>>>

    @PATCH("/blocklists/{blockIdx}")
    fun unBlock(
        @Path("blockIdx") blockIdx : Int
    ): Call<BaseResponse<String>>



    @POST("/reports")
    fun sendReport(
        @Body ReportRequest: Report
    ):Call<BaseResponse<ReportResponse>>

    // -------------------History -------------------------- //

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
    ) : Call<HistorySenderDetailResponse>
    @GET("/histories/{userIdx}/{type}/{typeIdx}")
    fun historyDetailList(
        @Path("userIdx") userIdx : Int,
        @Path("type") type : String,
        @Path("typeIdx") typeIdx : Int,
    ) : Call<HistoryDetailResponse>

    //-------------Alarm-----------------//
    @GET("/alarms")
    fun getAlarmList(
        @Query("userIdx") userIdx:Int
    ):Call<BaseResponse<ArrayList<Alarm>>>

    @GET("/alarms/{alarmIdx}")
    fun getAlarmInfo(
        @Path("alarmIdx") alarmIdx : Int,
        @Query("userIdx") userIdx : Int
    ):Call<BaseResponse<AlarmInfo>>

    //-----------fcm token-------------//
    @PATCH("/auth/token")
    fun postFcmToken(
        @Query("userIdx") userIdx: Int,
        @Body fcmRequest :String
    ):Call<BaseResponse<String>>

    @POST("/users/{userIdx}/fcm")
    fun getSystemPushAlarm(
        @Path("userIdx") userIdx : Int
    ):Call<BaseResponse<String>>

    //---------------sendService----------//
    @POST("/letters")
    fun sendLetter(
        @Body request : SendLetterRequest
    ):Call<BaseResponse<String>>

    @POST("/replies")
    fun sendReply(
        @Body request : SendReplyRequest
    ):Call<BaseResponse<String>>

    //오류 수정 필요
    @PATCH("/replies/{replyIdx}")
    fun deleteReply(
        @Path("replyIdx")  replyIdx :Int
    ):Call<BaseResponse<String>>

}