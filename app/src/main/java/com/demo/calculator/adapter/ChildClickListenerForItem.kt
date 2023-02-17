package com.demo.calculator.adapter

import android.content.Context
import android.view.View
import com.demo.calculator.data.HistoryCell

interface ChildClickListenerForItem {

    fun onItemClick(historyCell: HistoryCell)

    fun showColorPalette(view: View, status: Boolean, color: Int)

    fun setColor(view: View, color: Int)

    fun getContext(): Context
}