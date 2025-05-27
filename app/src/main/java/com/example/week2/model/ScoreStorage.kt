package com.example.week2.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object ScoreStorage {

    private const val FILE_NAME = "high_scores.json"
    private const val MAX_SCORES = 10

    fun loadScores(context: Context): MutableList<Score> {
        val file = File(context.filesDir, FILE_NAME)

        if (!file.exists()) return mutableListOf()

        return try {
            val json = file.readText()
            val type = object : TypeToken<MutableList<Score>>() {}.type
            Gson().fromJson(json, type) ?: mutableListOf()
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    fun saveScores(context: Context, scores: List<Score>) {
        val file = File(context.filesDir, FILE_NAME)
        val topScores = scores.sortedByDescending { it.value }.take(MAX_SCORES)
        val json = Gson().toJson(topScores)
        file.writeText(json)
    }

    fun addScore(context: Context, newScore: Score): Boolean {
        val scores = loadScores(context)

        // אם יש פחות מ-10 שיאים, פשוט נוסיף
        if (scores.size < MAX_SCORES) {
            scores.add(newScore)
            saveScores(context, scores)
            return true
        }

        // אם יש 10 — נבדוק אם השיא החדש טוב מהנמוך ביותר
        val lowestScore = scores.minByOrNull { it.value }
        return if (lowestScore != null && newScore.value > lowestScore.value) {
            scores.remove(lowestScore)
            scores.add(newScore)
            saveScores(context, scores)
            true
        } else {
            false
        }
    }
}
