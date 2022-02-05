package com.likefirst.btos.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Diary(
    @SerializedName("diaryIdx") val diaryIdx : Int,
    @SerializedName("emotionIdx") val emotionIdx : Int,
    @SerializedName("diaryDate") val diaryDate : String,
    @SerializedName("isPublic") val isPublic: Int,
    @SerializedName("content") val content :String,
    @SerializedName("doneList") val doneList :ArrayList<DoneList>

):Parcelable

@Parcelize
data class DoneList(
    @SerializedName("doneIdx") val doneIdx : Int,
    @SerializedName("content") val content : String,
):Parcelable

data class DiaryResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code : Int,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : ResultDiary
)

data class ResultDiary(
    @SerializedName("type") val type : String,
    @SerializedName("content") val content : Diary,
)