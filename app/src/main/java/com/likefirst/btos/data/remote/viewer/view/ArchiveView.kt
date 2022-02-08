package com.likefirst.btos.data.remote.viewer.view

import com.likefirst.btos.data.remote.viewer.response.ArchiveCalendar
import com.likefirst.btos.data.remote.viewer.response.ArchiveListDiaryList
import com.likefirst.btos.data.remote.viewer.response.ArchiveListPageInfo
import com.likefirst.btos.data.remote.viewer.response.ArchiveListResult
import com.likefirst.btos.ui.archive.ArchiveListRVAdapter

interface ArchiveCalendarView {
    fun onArchiveCalendarLoading()
    fun onArchiveCalendarSuccess(result: ArrayList<ArchiveCalendar>)
    fun onArchiveCalendarFailure(code : Int)
}

interface ArchiveListView{
    fun onArchiveListLoading()
    fun onArchiveListSuccess(result: ArrayList<ArchiveListResult>, pageInfo: ArchiveListPageInfo, adapter : ArchiveListRVAdapter)
    fun onArchiveListFailure(code: Int)
}