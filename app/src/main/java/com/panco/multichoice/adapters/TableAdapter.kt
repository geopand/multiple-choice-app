package com.panco.multichoice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.panco.multichoice.R
import com.panco.multichoice.models.Game
import java.text.SimpleDateFormat
import java.util.Date

class TableAdapter(private val dataSet: List<Game>) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gameId: TextView
        val startDate: TextView
        val gameScore: TextView

        init {
            gameId = view.findViewById(R.id.tvGameId)
            startDate = view.findViewById(R.id.tvDateStarted)
            gameScore = view.findViewById(R.id.tvGameScore)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.gameId.text = "Παιχνίδι: ${dataSet[position].gameId}"
        viewHolder.startDate.text = "Έναρξη: ${getDateTime(dataSet[position].dateStarted.toLong())}"
        viewHolder.gameScore.text = "Πόντοι: ${dataSet[position].score}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun getDateTime(date: Long): String? {
        try {
            val sdf = SimpleDateFormat("dd MMM ,yyyy HH:mm")
            val resultdate = Date(date)
            return sdf.format(resultdate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

}

//source: https://developer.android.com/develop/ui/views/layout/recyclerview#kotlin