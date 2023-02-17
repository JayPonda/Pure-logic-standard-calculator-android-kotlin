package com.demo.calculator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.calculator.R
import com.demo.calculator.data.HistoryCell

class ChildHistoryRecyclerViewAdaptor(private val stack: MutableList<HistoryCell>, private val listener: ChildClickListenerForItem):
    RecyclerView.Adapter<HistoryCellHandler>() {

    private lateinit var ctx: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryCellHandler {
        ctx = parent.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.history_template, parent, false)
        return HistoryCellHandler(view, listener, true)
    }

    override fun getItemCount(): Int {
        return stack.size
    }

    override fun onBindViewHolder(holder: HistoryCellHandler, position: Int) {
        val historyCell = stack[position]

        holder.historyCell = historyCell
        holder.postExpression.text = historyCell.getExpr()
        holder.buffer.text = historyCell.buffer
        holder.emptyView.visibility = View.GONE
    }
}