package com.demo.calculator.businessLogic

object HistoryOperation {

    private var groupId = 0
    private val expStack: MutableList<ExpCreatorParser> = mutableListOf()
    private lateinit var currentExpr: ExpCreatorParser

    fun newHistoryExpr(exp: String, newBuffer: String, cValue: MutableList<Double>, rValue: MutableList<String>): ExpCreatorParser{
        if(this::currentExpr.isInitialized) currentExpr.terminate()
        currentExpr = ExpCreatorParser(groupId++, exp, newBuffer, cValue, rValue)
        expStack.add(0, currentExpr)
        return currentExpr
    }

    /**
     * this function clears the expStack
     */
    fun clearHistoryStack(){
        expStack.clear()
        groupId = 0
    }

    /**
     * this function is used to access expStack from outside of this file
     */
    fun getStack() = expStack

}