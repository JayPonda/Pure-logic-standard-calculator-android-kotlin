package com.demo.calculator.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.calculator.R
import com.demo.calculator.businessLogic.ExpCreatorParser


class HistoryEqWrapperHandler(val view: View, private val listener: ChildClickListenerForItem): RecyclerView.ViewHolder(view){

    private val emptyView: View = view.findViewById(R.id.groupView)
    private val evalExpression: TextView = view.findViewById(R.id.evalExpression)
    private val detailedOperationRv: RecyclerView = view.findViewById(R.id.detailedOperationRv)
    private val expand: ImageButton = view.findViewById(R.id.expand)
    private var status: Boolean = false /* true: open, false: close */

    init {
        expand.setOnClickListener {
            status = !status
            expandOrCollapseList(status)
        }
        expandOrCollapseList(status, false)
    }

    private fun expandOrCollapseList(status: Boolean, init: Boolean = true){
        // up-down image button
        expand.animate().rotation(
            if(status) 180F else 0F
        ).interpolator = AccelerateDecelerateInterpolator()

        // recycler view
        if(init)
            when {
                status -> {
                    detailedOperationRv.alpha = 0.0f
                    detailedOperationRv.visibility = View.VISIBLE

                    // Start the animation
                    detailedOperationRv.animate()
                        .alpha(1.0f)
                        .setListener(null)
                }
                else -> {
                    detailedOperationRv.animate()
                        .alpha(0.0f)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                detailedOperationRv.visibility = View.GONE
                            }
                        })
                }
            }
        else
            detailedOperationRv.visibility =  if(status) View.VISIBLE else View.GONE
    }


    fun setContent(expCreatorParser: ExpCreatorParser, color: Int,context: Context){
        listener.setColor(emptyView, color)
        evalExpression.text = expCreatorParser.getExpr()
        detailedOperationRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        detailedOperationRv.adapter = ChildHistoryRecyclerViewAdaptor(expCreatorParser.getStack(), listener)
    }
}