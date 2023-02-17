package com.demo.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.calculator.adapter.ChildClickListenerForItem
import com.demo.calculator.adapter.ParentHistoryRecyclerViewAdaptor
import com.demo.calculator.businessLogic.CalculateParser.getImage
import com.demo.calculator.businessLogic.CalculateParser.parsingFromHistory
import com.demo.calculator.businessLogic.HistoryOperation
import com.demo.calculator.businessLogic.HistoryOperation.clearHistoryStack
import com.demo.calculator.data.HistoryCell
import com.demo.calculator.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var postExp: String
    private lateinit var buffer: String
    private lateinit var adapter: ParentHistoryRecyclerViewAdaptor
    private lateinit var operationImage: Pair<String, String>
    private var toast: Toast? = null

    private var isSet = false
    private lateinit var historyCell: HistoryCell

    private fun render(isReset: Boolean = false){
        binding.mainTemplate.postExpression.text = if(isReset) operationImage.first else postExp
        binding.mainTemplate.buffer.text = if(isReset) operationImage.second else buffer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        operationImage = getImage()
        postExp = intent.getStringExtra("postExp").toString()
        buffer = intent.getStringExtra("buffer").toString()

        render()

        adapter = ParentHistoryRecyclerViewAdaptor(HistoryOperation.getStack())

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.historyRecyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ChildClickListenerForItem{
            override fun onItemClick(historyCell: HistoryCell) {
                isSet = true
                this@HistoryActivity.historyCell = historyCell
                postExp = historyCell.getExpr()
                buffer = historyCell.buffer

                toast?.cancel()

                toast = Toast.makeText(binding.root.context, "calculator parameters changed", Toast.LENGTH_SHORT)
                toast?.show()
                render()
            }

            override fun showColorPalette(view: View, status: Boolean, color: Int) {
                view.visibility = if(status) View.VISIBLE else View.GONE
                setColor(view, color)
            }

            override fun setColor(view: View, color: Int) {
                view.backgroundTintList = getContext().resources.getColorStateList(
                    ParentHistoryRecyclerViewAdaptor.colorRow[color], null)
            }

            override fun getContext(): Context {
                return this@HistoryActivity
            }
        })
    }

    override fun onBackPressed() {
        when(isSet) {
            false -> {
                intent.putExtra("postExp", operationImage.first)
                intent.putExtra("buffer", operationImage.second)
            }
            true -> {
                parsingFromHistory(historyCell)
                intent.putExtra("postExp", postExp)
                intent.putExtra("buffer", buffer)
            }
        }
        setResult(RESULT_OK, intent)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.history_page_menu, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.delete_history -> {
                clearHistoryStack()
                adapter.notifyDataSetChanged()
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.scrollToTop -> {
                binding.historyRecyclerView.smoothScrollToPosition(0)
                return true
            }
            R.id.scrollToTopAndSavePrevious -> {
                isSet = false
                binding.historyRecyclerView.smoothScrollToPosition(0)
                render(true)
                Toast.makeText(this, "continue previous calculation", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}