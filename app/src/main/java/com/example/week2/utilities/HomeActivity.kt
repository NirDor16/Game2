package com.example.week2.utilities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.week2.HighScoresActivity
import com.example.week2.MainActivity
import com.example.week2.R

class HomeActivity : AppCompatActivity() {

    companion object {
        const val MODE_KEY = "MODE"
        const val MODE_SLOW = "slow"
        const val MODE_FAST = "fast"
        const val MODE_SENSOR = "sensor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescrean)

        findViewById<Button>(R.id.home_BTN_slow).setOnClickListener {
            startGameWithMode(MODE_SLOW)
        }

        findViewById<Button>(R.id.home_BTN_fast).setOnClickListener {
            startGameWithMode(MODE_FAST)
        }

        findViewById<Button>(R.id.home_BTN_sensor).setOnClickListener {
            startGameWithMode(MODE_SENSOR)
        }

        // כפתור חדש - טבלת שיאים
        findViewById<Button>(R.id.home_BTN_high_scores).setOnClickListener {
            val intent = Intent(this, HighScoresActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGameWithMode(mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MODE_KEY, mode)
        startActivity(intent)
        finish()
    }
}
