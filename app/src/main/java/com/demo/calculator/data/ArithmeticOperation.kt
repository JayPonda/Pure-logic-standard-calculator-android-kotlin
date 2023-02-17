package com.demo.calculator.data

import kotlin.math.pow

object ArithmeticOperation {

        const val DECIMAL: Byte = 0
        const val BINARY: Byte = 10
        const val OCTAL: Byte = 20
        const val HEX: Byte = 30

        const val ADD: Byte = 100
        const val SUB: Byte = 101
        const val MUL: Byte = 102
        const val DIV: Byte = 103
        const val NEG: Byte = 104
        const val RAISE: Byte = 105
        const val UPON: Byte = 106
        const val SQUARE: Byte = 107
        const val ROOT: Byte = 108
        const val PERCENTAGE: Byte = 109

        const val AND: Byte = 50
        const val OR: Byte = 51
        const val XOR: Byte = 52
        const val NOR: Byte = 53
        const val NAND: Byte = 54
        const val NOT: Byte = 55
        const val SHR: Byte = 56
        const val SHL: Byte = 57


    /**
     * general decimal operations
     */

    fun arithmeticOpr(a: Double, b: Double, opr: Byte): Double{

            return when(opr){
                ADD -> a + b
                SUB -> a - b
                MUL -> a * b
                DIV -> {
                    if(b == 0.0)
                        throw Exception(ArithmeticException("number can not divided by zero"))
                    return a / b
                }
                RAISE -> a.pow(b)
                else -> throw Exception("more values are given")
        }
    }

    fun arithmeticOpr(a: Double, opr: Byte): Double{
        return when (opr){
            NEG -> arithmeticOpr(a, -1.0, MUL)
            UPON -> arithmeticOpr(1.0, a, DIV)
            SQUARE -> arithmeticOpr(a, 2.0, RAISE)
            ROOT -> arithmeticOpr(a, 0.5, RAISE)
            PERCENTAGE -> arithmeticOpr(a, 100.0, DIV)
            else -> throw Exception("more values are given")
        }
    }

    /**
     * different formats operation
     */

    fun getFormattedValue(value: Int, format: Byte): String {
        return when(format) {
            BINARY -> Integer.toBinaryString(value)
            OCTAL -> Integer.toOctalString(value)
            HEX -> Integer.toHexString(value)
            else -> value.toString()
        }
    }

    fun getValue(value: String, format: Byte): Int{
        return when(format) {
            BINARY -> Integer.parseInt(value, 2)
            OCTAL -> Integer.parseInt(value, 8)
            HEX -> Integer.parseInt(value, 16)
            else -> Integer.parseInt(value, 10)
        }
    }

    fun bitOpr(aStr: String, bStr: String, format: Byte ,opr: Byte): Int{

        val a = getValue(aStr, format)
        val b = getValue(bStr, format)

        return when(opr){
            ADD -> a + b
            SUB -> a - b
            MUL -> a * b
            DIV -> {
                if(b == 0)
                    throw Exception(ArithmeticException("number can not divided by zero"))
                return a / b
            }
            AND -> a and b
            OR -> a or b
            XOR -> a xor b
            NAND -> (a and b).inv()
            NOR -> (a or b).inv()
            NOT -> a.inv()
            SHL -> a shr 1
            SHR -> a shl 1
            else -> throw Exception("more values are given")
        }
    }

}