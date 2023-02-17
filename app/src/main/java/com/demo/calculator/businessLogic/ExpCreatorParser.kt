package com.demo.calculator.businessLogic

import com.demo.calculator.data.HistoryCell

class ExpCreatorParser(

    private val groupId: Int, private var expr: String, private var buffer: String, cValue: MutableList<Double>, rValue: MutableList<String>) {

    private var expId = 0
    private val expStack: MutableList<HistoryCell> = mutableListOf()
    private var isTerminated = false

    init {
        expStack.add(HistoryCell(expId, expr, buffer, cValue, rValue))
        expr = expr.replace(Regex("( =)?$"), "")
    }
    
    private fun handleArtOpr(opr: String){
        expr += " $opr "
    }
    
    private fun handleHigherOpr(opr: String){
        expr += "$opr($expr)"
    }
    
    fun terminate(){
        if(!isTerminated){
            expr += " = $buffer"
            isTerminated = true
        }

    }

    fun writeHistory(exp: String, newBuffer: String, cValue: List<Double>, rValue: List<String>): ExpCreatorParser{
        val cell = HistoryCell(++expId, exp, newBuffer, cValue, rValue)
        expParser(cell.postExpression)
        expStack.add(cell)
        buffer = cell.buffer
        return this
    }

    private fun expParser(exp: String){
        val strAry = exp.replace( Regex("( =)?$"), "").split(" ")

        fun hOpr(v: String): Boolean{
            val match = Regex("^(sqr|sqrt|-|1/)").find(v)
            if(match != null){
                handleHigherOpr(match.value)
                return true
            }
                return false
        }

        // if it's function
        hOpr(strAry[0])

        if(strAry.size == 3){
            handleArtOpr(strAry[1])

            expr += strAry[2]
        }

    }

    /**
     * this function is used to access expStack from outside of this file
     */
    fun getStack(): MutableList<HistoryCell> = expStack

    fun getSize(): Int = expStack.size

    fun getExprId(): Int = groupId

    fun getExpr(): String = expr.replace("/", "\u00F7").replace("*", "\u00D7")

}