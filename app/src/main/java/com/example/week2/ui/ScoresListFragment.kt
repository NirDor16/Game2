package com.example.week2.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.week2.R
import com.example.week2.model.Score
import com.example.week2.model.ScoreStorage

class ScoresListFragment : Fragment() {

    interface OnScoreSelectedListener {
        fun onScoreSelected(score: Score)
    }

    private var listener: OnScoreSelectedListener? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ScoresAdapter
    private lateinit var emptyTextView: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreSelectedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnScoreSelectedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)

        recyclerView = view.findViewById(R.id.recyclerView_scores)
        emptyTextView = view.findViewById(R.id.highscore_empty_text)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val scores = ScoreStorage.loadScores(requireContext())
            .filter { it.value > 0 }

        if (scores.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyTextView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }

        adapter = ScoresAdapter(scores.toMutableList()) { selectedScore ->
            listener?.onScoreSelected(selectedScore)
        }

        recyclerView.adapter = adapter

        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
