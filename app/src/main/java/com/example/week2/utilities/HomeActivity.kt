package com.example.week2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

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
    }

    private fun startGameWithMode(mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MODE_KEY, mode)
        startActivity(intent)
        finish()
    }
}
