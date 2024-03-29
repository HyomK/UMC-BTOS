package com.likefirst.btos.ui.view.archive

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.likefirst.btos.data.entities.DiaryViewerInfo
import com.likefirst.btos.data.local.UserDatabase
import com.likefirst.btos.data.remote.viewer.response.ArchiveCalendar
import com.likefirst.btos.data.remote.viewer.response.ArchiveDiaryResult
import com.likefirst.btos.data.remote.viewer.service.ArchiveCalendarService
import com.likefirst.btos.data.remote.viewer.service.ArchiveDiaryService
import com.likefirst.btos.data.remote.viewer.view.ArchiveCalendarView
import com.likefirst.btos.data.remote.viewer.view.ArchiveDiaryView
import com.likefirst.btos.databinding.ItemArchiveCalendarVpBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.view.main.CustomDialogFragment
import com.likefirst.btos.ui.view.posting.DiaryActivity
import com.likefirst.btos.ui.view.posting.DiaryViewerActivity
import com.likefirst.btos.ui.view.splash.LoginActivity
import com.likefirst.btos.utils.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


class ArchiveCalendarItemFragment() : BaseFragment<ItemArchiveCalendarVpBinding>(ItemArchiveCalendarVpBinding::inflate)
, ArchiveCalendarView, ArchiveDiaryView{

    val viewMode by lazy {
        requireArguments().getInt("viewMode")
    }
    val pageIndex by lazy {
        requireArguments().getInt("pageIndex")
    }

    override fun initAfterBinding() {

        createDiaryList()
    }

    fun createDiaryList(){
        var type = ""
        when (viewMode){
            0 -> type = "doneList"
            1 -> type = "emotion"
        }
        val date = dateToStringMonth(getCalendar())
        val archiveCalendarService = ArchiveCalendarService()
        archiveCalendarService.setArchiveCalendarView(this)
        archiveCalendarService.getCalendar(getUserIdx(), date, type)
    }

    fun getCalendar() : Date{
        val calendar = Calendar.getInstance()
        val MONTH_TODAY = calendar.get(Calendar.MONTH)
        val YEAR_TODAY = calendar.get(Calendar.YEAR)

        if(MONTH_TODAY + pageIndex >= 0){
            val monthIndex = (MONTH_TODAY + pageIndex) % 12
            val year = (MONTH_TODAY + pageIndex) / 12 + YEAR_TODAY
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthIndex)
            return calendar.time
        } else {
            val monthIndexAbs = kotlin.math.abs(MONTH_TODAY + pageIndex)
            val year = YEAR_TODAY - ((monthIndexAbs-1) / 12) - 1
            val monthIndex = 12 - (monthIndexAbs % 12)
            if (monthIndex == 12){
                calendar.set(Calendar.MONTH, 0)
            } else {
                calendar.set(Calendar.MONTH, monthIndex)
            }
            calendar.set(Calendar.YEAR, year)
            return calendar.time
        }
    }

    fun createCalendarList(calendarInputList : ArrayList<ArchiveCalendar>) : ArrayList<ArchiveCalendar> {
        val customCalendar = CustomCalendar(getCalendar(), calendarInputList)
        customCalendar.initBaseCalendar()
        Log.d("calendarList", customCalendar.dateList.toString())
        return customCalendar.dateList
    }

    fun showLogoutDialog(message : String, tag : String){
        val dialog = CustomDialogFragment()
        val data = arrayOf("확인")
        dialog.arguments= bundleOf(
            "bodyContext" to  message,
            "btnData" to data
        )
        dialog.setButtonClickListener(object : CustomDialogFragment.OnButtonClickListener {
            override fun onButton1Clicked() {
                val gso = getGSO()
                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut()
                removeJwt()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                exitProcess(0)
            }

            override fun onButton2Clicked() {

            }
        })
        dialog.show(this.parentFragmentManager, tag)
    }

    override fun onArchiveCalendarLoading() {
    }

    override fun onArchiveCalendarSuccess(result: ArrayList<ArchiveCalendar>) {

        val calendarAdapter = ArchiveCalendarRVAdapter(createCalendarList(result), requireContext(), viewMode)

        binding.archiveCalendarLoadingView.visibility = View.GONE
        binding.archiveCalendarRv.apply {
            adapter = calendarAdapter
            layoutManager = GridLayoutManager(requireContext(), 7)
            calendarAdapter.setDate(getCalendar())
            calendarAdapter.setOnDateSelectedListener(object : ArchiveCalendarRVAdapter.CalendarDateSelectedListener {
                override fun onDateSelectedListener(date: Date, dayInt : Int) {
                    val calendar = GregorianCalendar.getInstance()
                    calendar.time = date
                    calendar.set(Calendar.DAY_OF_MONTH, dayInt)

                    // 오늘 날짜랑 비교 (1. calendar = GregorianCalendar.getInstance() : 0
                    //                   2. calendar > GregorianCalendar.getInstance() : 1
                    //                   3. calendar < GregorianCalendar.getInstance() : -1)
                    if(calendar.compareTo(GregorianCalendar.getInstance()) == 1){
                        Snackbar.make(view!!, "미래의 일기는 작성할 수 없어요!!!", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val dateString = dateToString(calendar.time)
                        val intent = Intent(requireContext(), DiaryActivity::class.java)
                        intent.putExtra("diaryDate", dateString)
                        startActivity(intent)
                    }
                }

                override fun onDiarySelectedListener(diaryIdx: Int) {
                    val service = ArchiveDiaryService()
                    service.setArchiveCalendarView(this@ArchiveCalendarItemFragment)
                    service.getDiary(diaryIdx)
                }
            })
        }
    }

    override fun onArchiveCalendarFailure(code : Int) {


        when (code){
            4000 -> Snackbar.make(requireView(), "일기를 불러오는데 실패하였습니다. 다시 시도해 주세요", Snackbar.LENGTH_SHORT).show()
            6000 -> showLogoutDialog("유효하지 않은 회원입니다. 다시 로그인 해 주세요", "onArchiveCalendarFailure Code:6000")
            6006 -> Snackbar.make(requireView(), "프리미엄 계정 가입이 필요합니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onArchiveDiaryLoading() {

    }

    override fun onArchiveDiarySuccess(result: ArchiveDiaryResult) {
        binding.archiveCalendarLoadingView.visibility = View.GONE
        var isPublic = false
        if (result.isPublic == 1){
            isPublic = true
        }
        val userDB = UserDatabase.getInstance(requireContext())!!.userDao()
        val intent = Intent(requireContext(), DiaryViewerActivity::class.java)
        intent.putExtra("diaryInfo", DiaryViewerInfo(userDB.getNickName()!!, result.emotionIdx, result.diaryDate, result.content, isPublic, result.doneList))
        intent.putExtra("diaryIdx", result.diaryIdx)
        startActivity(intent)
//        ArchiveCalendarFragment.pageIndexFlag = true
    }

    override fun onArchiveDiaryFailure(code: Int) {
        binding.archiveCalendarLoadingView.visibility = View.GONE
        when (code){
            4000 -> Snackbar.make(requireView(), "데이터베이스 연결에 실패하였습니다.", Snackbar.LENGTH_SHORT).show()
            6002 -> Snackbar.make(requireView(), "존재하지 않는 일기입니다. 개발자에게 문의해 주세요.", Snackbar.LENGTH_SHORT).show()
            6008 -> Snackbar.make(requireView(), "일기 복호화에 실패했습니다. 개발자에게 문의해 주세요.", Snackbar.LENGTH_SHORT).show()
        }
    }
}