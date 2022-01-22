package com.likefirst.btos.ui.posting

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likefirst.btos.databinding.ActivityDiaryViewerBinding
import com.likefirst.btos.ui.BaseActivity
import com.likefirst.btos.ui.main.MainActivity

class DiaryViewerActivity: BaseActivity<ActivityDiaryViewerBinding>(ActivityDiaryViewerBinding::inflate) {
    override fun initAfterBinding() {
        val doneListAdapter = DiaryViewerDoneListRVAdapter()
        binding.diaryViewerDoneListRv.apply {
            adapter = doneListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        binding.diaryViewerToolbar.toolbarBackIc.setOnClickListener {
            goToHome()
        }
    }

    override fun onBackPressed() {
        goToHome()
        super.onBackPressed()
    }

    fun goToHome(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}