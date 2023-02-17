package com.demo.calculator.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demo.calculator.R
import com.demo.calculator.data.HistoryCell

class HistoryCellHandler(view: View, private val listener: ChildClickListenerForItem, private val initialStatus: Boolean): RecyclerView.ViewHolder(view){

    lateinit var historyCell: HistoryCell

    val postExpression: TextView = view.findViewById(R.id.postQuestion)
    val buffer: TextView = view.findViewById(R.id.postAnswer)
    val emptyView: View = view.findViewById(R.id.groupView)

    init {
        view.setOnClickListener{
            listener.onItemClick(historyCell)
        }
        listener.showColorPalette(emptyView, initialStatus, 0)
    }

    fun setContent(hCell: HistoryCell, color: Int ){
        historyCell = hCell
        postExpression.text = historyCell.getExpr()
        buffer.text = historyCell.buffer
        if(initialStatus)
            listener.setColor(emptyView, color)
    }
}