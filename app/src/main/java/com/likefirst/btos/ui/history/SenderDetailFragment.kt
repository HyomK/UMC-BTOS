package com.likefirst.btos.ui.history

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.likefirst.btos.R
import com.likefirst.btos.data.entities.History
import com.likefirst.btos.databinding.FragmentHistorySenderDetailBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.main.MainActivity

class SenderDetailFragment: BaseFragment<FragmentHistorySenderDetailBinding>(FragmentHistorySenderDetailBinding::inflate) {

    val items = List(100, { i -> History(i, "부족하면 부족한대로 채우고 충분하면 충분한대로 매력 발산하면서 멋지게 살자. " +
                "부족하면 부족한대로 채우고 충분하면 충분한대로 매력 발산하면서 멋지게 살자.", "2021.12.12", "처음이", 1, 3) })

    override fun initAfterBinding() {
        val mActivity = activity as MainActivity

        val toolbar = requireActivity().findViewById<Toolbar>(R.id.history_toolbar)
        val back = toolbar.findViewById<ImageView>(R.id.history_back_iv)
        back.visibility = View.VISIBLE
        val search = toolbar.findViewById<ImageView>(R.id.history_search_iv)
        search.visibility = View.GONE

        binding.itemHistorySenderTitle.text = items[0].sender

        val recyclerViewAdapter = NoSenderRecyclerViewAdapter(context, items)
        binding.fragmentSenderRv.adapter = recyclerViewAdapter
        binding.fragmentSenderRv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )

        recyclerViewAdapter.setMyItemClickListener(object :
            NoSenderRecyclerViewAdapter.MyItemClickListener {
            override fun MoveToDetail(historyIdx: Int) {
                mActivity.let {
                    val intent = Intent(context,HistoryDetailActivity::class.java)
                    startActivity(intent)
                }
            }
        })
    }
}