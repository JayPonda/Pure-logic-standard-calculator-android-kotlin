package com.demo.calculator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.calculator.R
import com.demo.calculator.businessLogic.ExpCreatorParser

class ParentHistoryRecyclerViewAdaptor(private val stack: MutableList<ExpCreatorParser>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val directItem = 0
    private val wrapperRv = 1

    companion object{
        val colorRow: List<Int> = listOf(
            R.color.yellow, // yellow
            R.color.blue, // blue
            R.color.red, //red
            R.color.dark_blue, //dark blue
            R.color.green, //green
            R.color.pink //pink
        )
    }

    private lateinit var listener: ChildClickListenerForItem

    private lateinit var context: Context

    private fun getStatus(position: Int): Int{
        return if(stack[position].getSize() == 1) directItem else wrapperRv
    }

    fun setOnItemClickListener(listener: ChildClickListenerForItem){
        this.listener = listener
        this.context = listener.getContext()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            directItem ->  {
                val view = LayoutInflater.from(context).inflate(R.layout.history_template, parent, false)
                HistoryCellHandler(view, listener, true)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.inner_fragment_operation_rv, parent, false)
                HistoryEqWrapperHandler(view, listener)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val equStack = stack[position]
        val colorIndex = equStack.getExprId() % colorRow.size
        println(equStack.getExprId())

        when(getStatus(position)){
            directItem -> (holder as HistoryCellHandler).setContent(equStack.getStack()[0], colorIndex)
            else -> (holder as HistoryEqWrapperHandler).setContent(equStack, colorIndex, context)
        }
    }

    override fun getItemCount(): Int {
        return stack.size
    }

    override fun getItemViewType(position: Int): Int {
        return getStatus(position)
    }
}