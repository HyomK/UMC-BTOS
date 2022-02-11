package com.likefirst.btos.ui.history

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likefirst.btos.R
import com.likefirst.btos.data.entities.HistoryList
import com.likefirst.btos.data.remote.history.service.HistoryService
import com.likefirst.btos.data.remote.history.view.HistoryDetailView
import com.likefirst.btos.databinding.FragmentHistoryListBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.main.MainActivity

class HistoryDetailFragment(private val userIdx: Int,private val type: String,private val typeIdx: Int)
    : BaseFragment<FragmentHistoryListBinding>(FragmentHistoryListBinding::inflate), MainActivity.onBackPressedListener, HistoryDetailView {

    val positioning : Int = -1
    override fun initAfterBinding() {
        binding.historyToolbar.historyDetailBackIv.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        initHistoryDetailList()
    }

    private fun initHistoryDetailList() {
        val recyclerViewAdapter = HistoryDetailRecyclerViewAdapter(requireContext(), resources.getStringArray(
            R.array.emotionNames))
        binding.historyDetailRv.apply {
            adapter = recyclerViewAdapter
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        recyclerViewAdapter.setMyItemClickListener(object : HistoryDetailRecyclerViewAdapter.MyItemClickListener {
            override fun foundPositioning(position: Int) {
                Log.e("HISTORYDETAIL",recyclerViewAdapter.getPosition().toString())
                binding.historyDetailRv.scrollToPosition(position)
            }
        })
        val historyService = HistoryService()
        historyService.setHistoryDetailView(this)
        historyService.historyDetail(userIdx, type, typeIdx, recyclerViewAdapter)
    }

    override fun onBackPressed() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onHistoryDetailLoading() {

    }

    override fun onHistoryDetailSuccess(response: ArrayList<HistoryList>, recyclerViewAdapter: HistoryDetailRecyclerViewAdapter) {
        recyclerViewAdapter.setHistoryItems(response)
        recyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onHistoryDetailFailure(code: Int, message: String) {
    }
}