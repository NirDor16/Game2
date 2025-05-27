package com.example.week2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit

import com.example.week2.model.Score
import com.example.week2.ui.MapFragment
import com.example.week2.ui.ScoresListFragment
import com.example.week2.utilities.HomeActivity

class HighScoresActivity : AppCompatActivity(), ScoresListFragment.OnScoreSelectedListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.highscoresactivity)

        findViewById<Button>(R.id.highscores_BTN_home).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (!hasLocationPermission()) {
            requestLocationPermission()
        }

        val score = intent.getSerializableExtra("NEW_SCORE") as? Score

        supportFragmentManager.commit {
            replace(R.id.main_FRAME_list, ScoresListFragment())
            replace(R.id.main_FRAME_map, MapFragment())
        }

        // השהיה קלה כדי לוודא שה-Fragment נטען
        score?.let {
            window.decorView.postDelayed({
                val mapFragment = supportFragmentManager.findFragmentById(R.id.main_FRAME_map) as? MapFragment
                mapFragment?.updateLocation(it.latitude, it.longitude, "שיא חדש: ${it.name}")
            }, 500)
        }
    }


    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onScoreSelected(score: Score) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.main_FRAME_map) as? MapFragment
        mapFragment?.updateLocation(score.latitude, score.longitude)
    }
}
