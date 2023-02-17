package com.demo.calculator.businessLogic

import com.demo.calculator.data.HistoryCell
import kotlin.math.pow
import kotlin.math.sqrt

object CalculateParser {
    
//    memory status (51 - 80)
//    const val MEMORY_ENABLED = 10
//    const val MEMORY_DISABLED = 11
//    const val MEMORY_CLEAR = 12
//    const val MEMORY_MODIFIED = 13

//    memoryStack and historyStack are store previous memory and calculations
//    these attributes are used to displayed data in particular format
//    arrayOf(buffer, firstArg, operand, secondArg)

    /**
     * renderingString is attribute to store the string which displays on user's device
     * 0 + sqrt(4) as ("0", "+", "sqrt(4)")
     */
    private val renderedString = mutableListOf("0" ,"_", "")

    /**
     * calcValues are used to store main values which are used to in operations and internal process
     * equivalent values arrayOf(accumulator, operandId, secondValue)
     * arrayOf(0, 0, 2)
     *
     */
    private val calcValues = mutableListOf(0.0, -1.0, 0.0)

    /**
     * buffer is used to store the number which used typed
     * postExpression stores the string which user's device used to show an expression
     */
    private var buffer = "0"
    private var postExpression = ""

    /**
     * status index
     * 0 = accumulator
     * 1 = operand
     * 2 = argVal

     * formatChanged
     * 00 = 0 = no one is formatted
     * 10 = 1 = first one is formatted
     * 01 = 2 = second one is formatted
     * 11 = 3 = both are formatted
     *
     * isError shows the error status
     */
    private var statusIndex = 0
    private var formatChanged = 0
    private var isError = false
    private var currentStack: ExpCreatorParser? = null


    /**
     * roundTo is an extension function on String type to convert
     * x.0 -> x
     * x.000000000001 -> x.0
     */
    private fun String.roundTo(): String{
        val lst = this.split('.')
        var zero = ""
        if(lst.size == 2){
            if(lst[1].length == 12)
                Regex("[0]+\\d$").replace(lst[1], "0")
            zero = Regex("[0]+$").replace(lst[1], "")
        }
        return "${lst[0]}${if(zero == "") "" else ".$zero"}"
    }

    /**
     * roundTo is an extension function on Double datatype to convert
     * x.251212021001021250 -> "x.251212021001021250" -> "x.251212021001"
     * x.12 -> "x.12"
     * x.0 -> "x"
     */
    private fun Double.roundTo(): String{
        val first = this.toString().roundTo()
        if(first.length < 12)
            return first
        return "%.12f".format(this).roundTo()
    }

    /**
     * handle Error by modifying isError flag
     * handleError is generating error
     * clearError is clear error
     */

    private fun handleError(errorMessage: String = "ERROR"): Double{
        clearAll()
        buffer = errorMessage
        isError = true
        return 0.0
    }

    private fun clearError(){
        buffer = "0"
        isError = false
        formatChanged = 0
    }

    /**
     * resetBuffer is resetting buffer with "0"
     *
     * suppose is format changed then buffer should be initialized
     * if statusIndex is 2 then it assign the value to rendingString and then initialized
     */
    private fun resetBuffer(int: Int){
        if(formatChanged == int || formatChanged == 3){

            if(!isError && statusIndex == 0){
                if (currentStack == null){
                    currentStack = HistoryOperation.newHistoryExpr("$postExpression =", buffer.roundTo(), calcValues, renderedString)
                }
                else{
                    currentStack?.writeHistory("$postExpression =", buffer.roundTo(), calcValues, renderedString)?.terminate()
                }

            }

            buffer = "0"
            formatChanged = 0
            if(int == 2)
                renderedString[2] = buffer
        }
    }

    /**
     * This function is used to handle adding literal to buffer
     * old value + new value / explanation
     * 0 + 1 -> 01
     * 0 + . -> 0.
     * 0. + . -> 0.
     *
     * 2 + 1 -> 1 / sqrt(4) formatChange 10 -> 1, overwrite with 1
     * "+" + 1 -> 1 / statusIndex 1 to 2 , reset buffer, set 1
     *
     */
    fun enterValue(char: Char): Pair<String, String>{
        // if error is there then need to clear it first
        if(isError) clearError()

        //if format update is there and adding literal then need to reset buffer with statusIndex 0 or 2
        if(statusIndex == 0) resetBuffer(1)
        else if(statusIndex == 2) resetBuffer(2)

        // if statusIndex is 0 or 2 then string is concatenated
        if(statusIndex == 0 || statusIndex == 2){
            if(buffer == "0")
                buffer = if(char == '.') "0." else char.toString()
            else if((char != '.' || !buffer.contains('.')) && buffer.length < 16)
                buffer += char
        }
        // if statusIndex transform from 1 to 2 then your first need to reset buffer
        else{
            statusIndex = 2
            buffer = if(char == '.') "0." else char.toString()
            postExpression = renderedString[0] + " " + renderedString[1]
        }

        // _ or acc "operand" ,x + y -> x_y
        return Pair(postExpression, buffer)
    }

    /**
     * if delete is pressed then this function is pressed
     *
     * if error is then need to clear first
     *
     * if operand is not there then gives new substring without last literal
     */
    fun delete(): Pair<String, String>{
        if(isError) clearError()
        if(statusIndex != 1)
            if(buffer.isNotEmpty() && buffer != "0"){
                buffer = buffer.substring(0, buffer.length - 1)
                if(buffer.isEmpty()) buffer = "0"
            }


        // _,xyz -> xy
        return Pair(postExpression, buffer)
    }

    /**
     * it is just clear the buffer
     */
    fun clear(): Pair<String, String>{
        buffer = "0"

        // _, "0"
        return Pair(postExpression, buffer)
    }

    /**
     * it is reinitialize all attributes
     */
    fun clearAll(): Pair<String, String>{
        isError = false
        postExpression = ""
        buffer = "0"
        statusIndex = 0
        formatChanged = 0
        renderedString[0] = "0"
        renderedString[1] = "_"
        renderedString[2] = ""
        calcValues[0] = 0.0
        calcValues[1] = -1.0
        calcValues[2] = 0.0

        // "", "0"
        return Pair(postExpression, buffer)
    }

    /**
     * this function is used to re-initialize the variables from history using HistoryCell object
     */
    fun parsingFromHistory(hs: HistoryCell): Pair<String ,String> {
        isError = false
        postExpression = hs.postExpression
        buffer = hs.buffer
        statusIndex = 0
        formatChanged = 0
        renderedString[0] = hs.firstRenderedOperand
        renderedString[1] = hs.operandTextCode
        renderedString[2] = hs.secondRenderedOperand
        calcValues[0] = hs.firstOperand
        calcValues[1] = hs.operatorCode.toDouble()
        calcValues[2] = hs.secondOperand

        return Pair(postExpression, buffer)
    }


    fun getImage() = Pair(postExpression, buffer)

    /**
     * this is the core logic of performing arithmetic operation
     * acc = acc "operand" secondValue
     * if error occurred then handle and return
     */
    private fun processOperation(){
        calcValues[0] = when(calcValues[1]){
            0.0 -> calcValues[0] + calcValues[2]
            1.0 -> calcValues[0] - calcValues[2]
            2.0 -> calcValues[0] * calcValues[2]
            else -> {
                if(calcValues[2] == 0.0) {
                    handleError()
                    return
                }
                else calcValues[0] / calcValues[2]
            }
        }

        renderedString[0] = calcValues[0].roundTo()

        buffer = renderedString[0]
    }

    /**
     * this function is used to manage if update operand or proceed operation
     * 1 + "old operand" + "new operand" -> 1 + "new operand"
     * 1 + "old operand" + 5 + "new operand" or 1 + "new operand" -> call mainCondition
     *
     * if error occurred then return
     */
    private fun operationManager(string: String, newOperand: Double){
        when (statusIndex) {
            1 -> {
                calcValues[1] = newOperand
                renderedString[1] = string
                postExpression = renderedString[0] + " " + renderedString[1]
            }
            else -> {
                // if error status is shown then clear it first
                if(isError) clearError()

                /*
                 if formatChanged is
                 00 || 01 -> calcValue[acc] <- buffer  or
                 00 || 10 -> calcValue[secondValue] <- buffer
                 if used higher operation like sqrt then already calValue and renderString has updated Value
                 so skip status 11
                 */
                if(formatChanged == 0 || formatChanged == if(statusIndex == 0) 2 else 1){
                    calcValues[statusIndex] = buffer.toDouble()
                    renderedString[statusIndex] = calcValues[statusIndex].roundTo()
                }

                // if acc + "operand" + secondValue + "new operand" -> processOperation(acc <- acc + "operand" + secondValue)
                if(statusIndex == 2) {
                    val exp = "${renderedString.joinToString(" ")} ="
                    processOperation()
                    when {
                        isError -> return
                        else -> {
                            when (currentStack) {
                                null -> currentStack = HistoryOperation.newHistoryExpr(exp, buffer, calcValues, renderedString)
                                else -> currentStack?.writeHistory(exp, buffer, calcValues, renderedString)
                            }
                        }
                    }
                }

                calcValues[1] = newOperand
                renderedString[1] = string
                postExpression = renderedString[0] + " " + renderedString[1]

                // if value already calculated then reset formatChange
                if(statusIndex == 2)
                    formatChanged = 0
            }
        }
        if(isError)
            return
        statusIndex = 1
    }

    /**
     * operation are handled by the operand
     *
     * + = 0
     * - = 1
     * * = 2
     * / = 3
     *
     */
    fun coreArithmeticOperation(operand: Double): Pair<String, String>{
        when(operand){
            0.0 -> operationManager("+", operand)
            1.0 -> operationManager("-", operand)
            2.0 -> operationManager("*", operand)
            3.0 -> operationManager("/", operand)
        }

        // acc + "operand", buffer
        // acc + "operand" + secondValue, buffer
        return Pair(postExpression, buffer)
    }

    /**
     * this function is handled the core logic behind the operation
     *
     * acc + "sqrt" -> acc = "sqrt(acc)"
     * acc + "operand" + secondValue + "sqrt" = acc + "operand" + "sqrt(secondValue)"
     *
     * here error can't possible because it is already handled in previous function
     */
    private fun higherCore(operand: Int){
        when(operand){
            10 -> {
                calcValues[statusIndex] = 1 / calcValues[statusIndex]
                renderedString[statusIndex] = "1/(${renderedString[statusIndex]})"
                buffer = calcValues[statusIndex].toString()
            }
            11 -> {
                calcValues[statusIndex] *= -1.0
                renderedString[statusIndex] = when {
                    calcValues[statusIndex] < 0 -> calcValues[statusIndex].roundTo()
                    else -> calcValues[statusIndex].roundTo()
                }
                buffer = calcValues[statusIndex].roundTo()
            }
            12 -> {
                calcValues[statusIndex] = calcValues[statusIndex].pow(2)
                renderedString[statusIndex] = "sqr(${renderedString[statusIndex]})"
                buffer = calcValues[statusIndex].roundTo()
            }
            13 -> {
                calcValues[statusIndex] = sqrt(calcValues[statusIndex])
                renderedString[statusIndex] = "sqrt(${renderedString[statusIndex]})"
                buffer = calcValues[statusIndex].roundTo()
            }
            14 -> {
                calcValues[statusIndex] /= 100.0
                renderedString[statusIndex] = calcValues[statusIndex].roundTo()
                buffer = renderedString[statusIndex]
            }
        }
    }

    /**
     * higherOperation is used to update values in rendered string
     *
     * if formatChanged value is
     *
     * 00 || 01 -> initialized calValues["acc"] and renderedString["acc"]
     * update formatChanged to 00 -> 10 or 01 -> 11
     *
     * 00 || 10 -> initialized calValues["secondValue"] and renderedString["secondValue"]
     * update formatChanged to 00 -> 01 or 10 -> 11
     */
    private fun higherOperation(operand: Int){
        val formatVal = if(statusIndex == 0) 2 else 1

        if(formatChanged == 0 || formatChanged == formatVal){
            renderedString[statusIndex] = buffer
            calcValues[statusIndex] = buffer.toDouble()
            formatChanged = when (statusIndex){
                0 -> if(formatChanged == 0) 1 else 3
                else -> if(formatChanged == 0) 2 else 3
            }
        }
        higherCore(operand)
    }

    /**
     * this is the function which handle operation with safety errors
     *
     * 1/x = 10
     * -x = 11
     * sqr(x) = 12
     * sqrt(x) = 13
     * %x = 14
     */
    fun higherArithmeticOperation(operand: Int):Pair<String, String>{
        if(isError) clearError()

        // if(a/x where x = 0.0) -> true
        // if(sqrt(x) where x < 0) -> true
        fun checkCondition(): Boolean{
            return when(operand){
                10 -> buffer.toDouble() == 0.0
                else -> buffer.toDouble() < 0
            }
        }

        when(operand){
            10, 13 -> {
                when (statusIndex) {
                    0 -> {
                        if(checkCondition()) handleError("INVALID VALUE")
                        else {
                            higherOperation(operand)
                            postExpression = renderedString[0]
                        }
                    }
                    else -> {
                        if(checkCondition()) handleError("INVALID VALUE")
                        else {
                            if(statusIndex == 1) statusIndex = 2
                            higherOperation(operand)
                            postExpression = renderedString.joinToString(" ")
                        }
                    }
                }
            }
            11, 12, 14 -> {
                when (statusIndex) {
                    0 -> {
                            higherOperation(operand)
                            postExpression = renderedString[0]
                    }
                    else -> {
                            if(statusIndex == 1) statusIndex = 2
                            higherOperation(operand)
                            postExpression = when(operand){
                                12 -> renderedString.joinToString(" ")
                                else -> renderedString[0] + " " + renderedString[1]
                            }
                    }
                }
            }
        }

        // "sqrt(acc)", buffer
        // "sqrt(acc)" "operand" "sqrt(acc)"
        return Pair(postExpression, buffer)
    }

    /**
     * this function is used to call processOperation() and reset formatChanged
     */
    private fun farewellConditionForEqual(shouldPutValue: Boolean): String{
        // if shouldPutValue is true then initialized secondValue if 00 or 10 is there
        if(shouldPutValue && (formatChanged == 0 || formatChanged == 1)) {
            renderedString[2] = buffer
            calcValues[2] = buffer.toDouble()
        }

        val expr = "${renderedString.joinToString(" ")} ="
        processOperation()
        formatChanged = 0
        return expr
    }

    /**
     * this function is used to handle equal condition
     *
     * "buffer" -> initialize calValues and renderedString
     *  acc + "new operand" -> initialize secondValue as buffer
     * "buffer" + "old operand" + "old secondValue / buffer" -> initialize calValue and call farewellConditionForEqual()
     */
    fun equal(): Pair<String, String>{
        var expr = ""
        var isDiff = false
        if(isError) clearError()

        when(statusIndex) {
            0 -> {
                if (formatChanged == 0) {
                    calcValues[0] = buffer.toDouble()
                    renderedString[0] = calcValues[0].roundTo()
                }

                val preVel = renderedString[0]

                postExpression =
                    when {
                        renderedString[1] != "_" -> {
                            expr = farewellConditionForEqual(false)
                            when {
                                isError -> ""
                                else -> "$preVel ${renderedString[1]} ${renderedString[2]} ="
                            }
                        }
                        else -> {
                            isDiff = true
                            "$preVel ="
                        }
                    }
            }
            1, 2 -> {
                val preVel = renderedString[0]
                expr = farewellConditionForEqual(true)

                statusIndex = 0
                postExpression = when {
                    isError -> ""
                    else -> "$preVel ${renderedString[1]} ${renderedString[2]} ="
                }
            }
        }

        if(!isError){
            when {
                isDiff -> {
                    when (currentStack) {
                        null -> currentStack = HistoryOperation.newHistoryExpr(postExpression, calcValues[0].roundTo(), calcValues, renderedString)
                        else -> currentStack?.writeHistory(postExpression, calcValues[0].roundTo(), calcValues, renderedString)?.terminate()
                    }
                }
                else -> {
                    when(currentStack){
                        null -> HistoryOperation.newHistoryExpr(expr, buffer.roundTo(), calcValues, renderedString)
                        else -> currentStack?.writeHistory(expr, buffer.roundTo(), calcValues, renderedString)?.terminate()
                    }
                }
            }
            currentStack = null
        }

        // acc "="
        // acc "operand" secondValue "="
        return Pair(postExpression, buffer)
    }


}