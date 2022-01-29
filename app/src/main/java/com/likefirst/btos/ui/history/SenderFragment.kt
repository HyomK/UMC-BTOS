package com.likefirst.btos.ui.history

import androidx.recyclerview.widget.LinearLayoutManager
import com.likefirst.btos.R
import com.likefirst.btos.data.entities.History
import com.likefirst.btos.databinding.FragmentHistorySenderBinding
import com.likefirst.btos.ui.BaseFragment
import com.likefirst.btos.ui.main.MainActivity

class SenderFragment: BaseFragment<FragmentHistorySenderBinding>(FragmentHistorySenderBinding::inflate) {

    val items = List(100, {i -> History(i,"부족하면 부족한대로 채우고 충분하면 충분한대로 매력 발산하면서 멋지게 살자." +
            "부족하면 부족한대로 채우고 충분하면 충분한대로 매력 발산하면서 멋지게 살자.","2021.12.12","처음이",1,3)  })

    override fun initAfterBinding() {
        val mActivity = activity as MainActivity

        val recyclerViewAdapter = SenderRecyclerViewAdapter(context, items)
        binding.fragmentSenderRv.adapter = recyclerViewAdapter
        binding.fragmentSenderRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)

        recyclerViewAdapter.setMyItemClickListener(object : SenderRecyclerViewAdapter.MyItemClickListener{
            override fun MoveToSenderDetail(historyIdx: Int) {
                mActivity.supportFragmentManager.beginTransaction()
                    .add(R.id.history_fragment,  SenderDetailFragment(), "senderdetail")
                    .addToBackStack(null)
                    .commit()
            }
        })
    }
}