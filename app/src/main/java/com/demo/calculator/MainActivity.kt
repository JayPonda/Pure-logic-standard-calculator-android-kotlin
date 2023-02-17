package com.demo.calculator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.demo.calculator.businessLogic.CalculateParser.clear
import com.demo.calculator.businessLogic.CalculateParser.clearAll
import com.demo.calculator.businessLogic.CalculateParser.coreArithmeticOperation
import com.demo.calculator.businessLogic.CalculateParser.delete
import com.demo.calculator.businessLogic.CalculateParser.enterValue
import com.demo.calculator.businessLogic.CalculateParser.equal
import com.demo.calculator.businessLogic.CalculateParser.higherArithmeticOperation
import com.demo.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // private var memorySheet = false

    private fun render(newStatus: Pair<String, String>){
        binding.mainTemplate.buffer.text = newStatus.second
        binding.mainTemplate.postExpression.text = when{
            newStatus.second.matches("[a-zA-Z ]{3}".toRegex()) -> ""
            else -> newStatus.first.replace("/", "\u00F7").replace("*", "\u00D7")
        }
    }

    @JvmName("render1")
    private fun render(newStatus: Pair<String?, String?>){
        binding.mainTemplate.buffer.text = newStatus.second.toString()
        binding.mainTemplate.postExpression.text = when{
            newStatus.second.toString().matches("[a-zA-Z ]{3}".toRegex()) -> ""
            else -> newStatus.first?.replace("/", "\u00F7")?.replace("*", "\u00D7")
        }
    }

    private val editedDataResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            results : ActivityResult? ->
        when (results?.resultCode) {
            RESULT_OK -> {
                try{
                    val postExp = results.data?.getStringExtra("postExp")
                    val buffer = results.data?.getStringExtra("buffer")
                    render(Pair(postExp, buffer))
                } catch (e: Exception){
                    Toast.makeText(this, "EXCEPTION OCCURRED", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "UNEXPECTED RESULTS FOUND", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainTemplate.buffer.text = "0"

//        //memory buttons
//
//        binding.mc.setOnClickListener {
//            memoryOperation("mc")
//        }
//        binding.ms.setOnClickListener {
//            memoryOperation("ms")
//        }
//        binding.mPlus.setOnClickListener {
//            memoryOperation("m+")
//        }
//        binding.mMinus.setOnClickListener {
//            memoryOperation("m-")
//        }
//        binding.mr.setOnClickListener {
//            memoryOperation("mr")
//        }
//        binding.memoryShow.setOnClickListener {
//            bottomSheetManager()
//        }



        // calculator number operation

        binding.zero.setOnClickListener{
            render(enterValue('0'))
        }

        binding.one.setOnClickListener{
            render(enterValue('1'))
        }

        binding.two.setOnClickListener{
            render(enterValue('2'))
        }

        binding.three.setOnClickListener{
            render(enterValue('3'))
        }

        binding.four.setOnClickListener{
            render(enterValue('4'))
        }

        binding.five.setOnClickListener{
            render(enterValue('5'))
        }

        binding.six.setOnClickListener{
            render(enterValue('6'))
        }

        binding.seven.setOnClickListener{
            render(enterValue('7'))
        }

        binding.eight.setOnClickListener{
            render(enterValue('8'))
        }

        binding.nine.setOnClickListener{
            render(enterValue('9'))
        }

        binding.dot.setOnClickListener{
            render(enterValue('.'))
        }


        //handle arithmetic operations

        binding.plus.setOnClickListener {
            render(coreArithmeticOperation(0.0))
        }
        binding.minus.setOnClickListener {
            render(coreArithmeticOperation(1.0))
        }
        binding.multiply.setOnClickListener {
            render(coreArithmeticOperation(2.0))
        }
        binding.divide.setOnClickListener {
            render(coreArithmeticOperation(3.0))
        }


        // handle higher order operations
        binding.square.setOnClickListener {
            render(higherArithmeticOperation(12))
        }
        binding.squareRoot.setOnClickListener {
            render(higherArithmeticOperation(13))
        }
        binding.invertNumber.setOnClickListener {
            render(higherArithmeticOperation(10))
        }
        binding.percentage.setOnClickListener {
            render(higherArithmeticOperation(14))
        }
        binding.negative.setOnClickListener {
            render(higherArithmeticOperation(11))
        }


        // handle command based Operations
        binding.ce.setOnClickListener {
            render(clear())
        }
        binding.c.setOnClickListener {
            render(clearAll())
        }
        binding.delete.setOnClickListener {
            render(delete())
        }
        binding.equal.setOnClickListener {
            render(equal())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menus, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
        if (id == R.id.toggle_history) {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("postExp", binding.mainTemplate.postExpression.text.toString())
            intent.putExtra("buffer", binding.mainTemplate.buffer.text.toString())
            editedDataResult.launch(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}