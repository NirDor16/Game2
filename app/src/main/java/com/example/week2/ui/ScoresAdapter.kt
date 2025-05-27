package com.example.week2.ui

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.week2.R
import com.example.week2.model.Score
import java.util.Locale
import java.util.concurrent.Executors

class ScoresAdapter(
    private val scores: MutableList<Score>,
    private val onItemClick: (Score) -> Unit
) : RecyclerView.Adapter<ScoresAdapter.ScoreViewHolder>() {

    private val geocoderExecutor = Executors.newSingleThreadExecutor()

    inner class ScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameText: TextView = view.findViewById(R.id.item_LBL_name)
        private val scoreText: TextView = view.findViewById(R.id.item_LBL_score)
        private val locationText: TextView = view.findViewById(R.id.item_LBL_location)

        fun bind(score: Score, position: Int) {
            nameText.text = "${position + 1}. ${score.name}"
            scoreText.text = score.value.toString()
            locationText.text = "מאתר מיקום..."

            geocoderExecutor.execute {
                try {
                    val geocoder = Geocoder(itemView.context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(score.latitude, score.longitude, 1)
                    val locationName = if (!addresses.isNullOrEmpty()) {
                        addresses[0].locality ?: addresses[0].adminArea ?: "מיקום לא ידוע"
                    } else {
                        "מיקום לא ידוע"
                    }

                    itemView.post {
                        locationText.text = locationName
                    }
                } catch (e: Exception) {
                    itemView.post {
                        locationText.text = "שגיאת מיקום"
                    }
                }
            }

            itemView.setOnClickListener { onItemClick(score) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_score, parent, false)
        return ScoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.bind(scores[position], position)
    }

    override fun getItemCount(): Int = scores.size

    fun updateData(newScores: List<Score>) {
        scores.clear()
        scores.addAll(newScores)
        notifyDataSetChanged()
    }
}
