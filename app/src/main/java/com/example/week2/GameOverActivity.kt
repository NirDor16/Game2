package com.example.week2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.week2.model.Score
import com.example.week2.model.ScoreStorage

class GameOverActivity : AppCompatActivity() {

    private var finalScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        finalScore = intent.getIntExtra("FINAL_SCORE", 0)

        val nameInput = findViewById<EditText>(R.id.gameover_ET_name)
        val sendButton = findViewById<Button>(R.id.gameover_BTN_send)

        sendButton.setOnClickListener {
            val playerName = nameInput.text.toString().ifBlank { "Player" }

            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val location = if (hasPermission)
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            else null

            val latitude = location?.latitude ?: 0.0
            val longitude = location?.longitude ?: 0.0

            val newScore = Score(playerName, finalScore, latitude, longitude)
            ScoreStorage.addScore(this, newScore)

            val intent = Intent(this, HighScoresActivity::class.java)
            intent.putExtra("NEW_SCORE", newScore)
            startActivity(intent)
            finish()
        }
    }
}
