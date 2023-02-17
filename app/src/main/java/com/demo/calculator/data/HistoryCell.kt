package com.demo.calculator.data

data class HistoryCell(
    val id: Int,
    val postExpression: String,
    val buffer: String,
    val firstOperand: Double,
    val operatorCode: Byte,
    val secondOperand: Double,
    val firstRenderedOperand: String,
    val operandTextCode: String,
    val secondRenderedOperand: String
){
  constructor(id: Int, postExpression: String, buffer: String, calkValues: List<Double>, randValues: List<String>):
          this(id, postExpression, buffer, calkValues[0], calkValues[1].toInt().toByte(), calkValues[2], randValues[0], randValues[1], randValues[2])

    fun getExpr() = postExpression.replace("/", "\u00F7").replace("*", "\u00D7")
}
