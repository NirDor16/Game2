package com.example.week2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.week2.logic.GameManager
import com.example.week2.model.Score
import com.example.week2.model.ScoreStorage
import com.example.week2.utilities.*
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.example.week2.utilities.BackgroundMusicPlayer
import com.example.week2.utilities.SignalManager
import com.example.week2.utilities.SingleSoundPlayer
import com.example.week2.utilities.TiltDetector
import com.example.week2.interfaces.TiltCallback
import com.example.week2.utilities.HomeActivity
import com.example.week2.GameOverActivity


class MainActivity : AppCompatActivity() {

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton
    private lateinit var main_IMG_cars: Array<AppCompatImageView>
    private lateinit var hydrants: Array<Array<ImageView>>
    private lateinit var criminals: Array<Array<ImageView>>
    private lateinit var scoreTextView: TextView

    private lateinit var gameManager: GameManager
    private lateinit var soundPlayer: SingleSoundPlayer
    private lateinit var backgroundMusicPlayer: BackgroundMusicPlayer
    private var tiltDetector: TiltDetector? = null

    private val handler = Handler(Looper.getMainLooper())
    private var interval: Long = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            //SignalManager.init(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BackgroundMusicPlayer.init(this)
        backgroundMusicPlayer = BackgroundMusicPlayer.getInstance()
        backgroundMusicPlayer.setResourceId(R.raw.background_music)

        soundPlayer = SingleSoundPlayer(this)

        findViews()
        initGame()
        initViews()
        handleGameMode()

        findViewById<View>(R.id.main).post {
            if (main_IMG_cars.all { it.height > 0 && it.width > 0 }) {

                // הפעלת החיישן רק כשהמשחק מתחיל באמת
                tiltDetector?.start()

                startGameLoop()
            } else {
                Toast.makeText(this, "שגיאה: לא הצלחנו להתחיל את המשחק", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun handleGameMode() {
        when (intent.getStringExtra(HomeActivity.MODE_KEY)) {
            HomeActivity.MODE_FAST -> interval = 500L
            HomeActivity.MODE_SLOW -> interval = 1000L
            HomeActivity.MODE_SENSOR -> {
                interval = 700L
                main_FAB_left.visibility = View.INVISIBLE
                main_FAB_right.visibility = View.INVISIBLE

                tiltDetector = TiltDetector(this, object : TiltCallback {
                    override fun tiltLeft() {
                        gameManager.moveLeft()
                    }

                    override fun tiltRight() {
                        gameManager.moveRight()
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()

        tiltDetector?.start()
        backgroundMusicPlayer.playMusic()
    }

    override fun onPause() {
        super.onPause()
        tiltDetector?.stop()
        backgroundMusicPlayer.stopMusic()
    }

    private fun initGame() {
        gameManager = GameManager(main_IMG_cars, hydrants, criminals)
    }

    private fun initViews() {
        main_FAB_left.setOnClickListener { gameManager.moveLeft() }
        main_FAB_right.setOnClickListener { gameManager.moveRight() }
    }

    private fun startGameLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val hits = gameManager.updateHydrants()
                val scored = gameManager.updateCriminals()

                if (hits.isNotEmpty()) {
                    val alive = gameManager.onHit()
                    updateHearts()
                    SignalManager.toast(this@MainActivity, "התנגשות!!")
                    SignalManager.vibrate(this@MainActivity)
                    soundPlayer.playSound(R.raw.boom)

                    if (!alive) {
                        SignalManager.toast(this@MainActivity, "Game Over!")
                        backgroundMusicPlayer.stopMusic()
                        handler.removeCallbacksAndMessages(null)

                        val intent = Intent(this@MainActivity, GameOverActivity::class.java)
                        intent.putExtra("FINAL_SCORE", gameManager.getScore())
                        startActivity(intent)
                        finish()
                        return
                    }
                }

                if (scored) {
                    updateScore()
                }

                handler.postDelayed(this, interval)
            }
        }, interval)
    }


    private fun updateHearts() {
        for (i in main_IMG_hearts.indices) {
            main_IMG_hearts[i].visibility = if (i < gameManager.getLives()) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateScore() {
        scoreTextView.text = gameManager.getScore().toString()
    }



    private fun findViews() {
            main_IMG_hearts = arrayOf(
                findViewById(R.id.main_IMG_heart0),
                findViewById(R.id.main_IMG_heart1),
                findViewById(R.id.main_IMG_heart2)
            )

            main_FAB_left = findViewById(R.id.main_FAB_left)
            main_FAB_right = findViewById(R.id.main_FAB_right)

            main_IMG_cars = arrayOf(
                findViewById(R.id.main_IMG_car0),
                findViewById(R.id.main_IMG_car1),
                findViewById(R.id.main_IMG_car2),
                findViewById(R.id.main_IMG_car3),
                findViewById(R.id.main_IMG_car4)
            )

            scoreTextView = findViewById(R.id.main_LBL_score)

            hydrants = arrayOf(
                arrayOf(
                    findViewById(R.id.hydrant_00),
                    findViewById(R.id.hydrant_01),
                    findViewById(R.id.hydrant_02),
                    findViewById(R.id.hydrant_03),
                    findViewById(R.id.hydrant_04),
                    findViewById(R.id.hydrant_05),
                    findViewById(R.id.hydrant_06),
                    findViewById(R.id.hydrant_07)
                ),
                arrayOf(
                    findViewById(R.id.hydrant_08),
                    findViewById(R.id.hydrant_09),
                    findViewById(R.id.hydrant_10),
                    findViewById(R.id.hydrant_11),
                    findViewById(R.id.hydrant_12),
                    findViewById(R.id.hydrant_13),
                    findViewById(R.id.hydrant_14),
                    findViewById(R.id.hydrant_15)
                ),
                arrayOf(
                    findViewById(R.id.hydrant_16),
                    findViewById(R.id.hydrant_17),
                    findViewById(R.id.hydrant_18),
                    findViewById(R.id.hydrant_19),
                    findViewById(R.id.hydrant_20),
                    findViewById(R.id.hydrant_21),
                    findViewById(R.id.hydrant_22),
                    findViewById(R.id.hydrant_23)
                ),
                arrayOf(
                    findViewById(R.id.hydrant_24),
                    findViewById(R.id.hydrant_25),
                    findViewById(R.id.hydrant_26),
                    findViewById(R.id.hydrant_27),
                    findViewById(R.id.hydrant_28),
                    findViewById(R.id.hydrant_29),
                    findViewById(R.id.hydrant_30),
                    findViewById(R.id.hydrant_31)
                ),
                arrayOf(
                    findViewById(R.id.hydrant_32),
                    findViewById(R.id.hydrant_33),
                    findViewById(R.id.hydrant_34),
                    findViewById(R.id.hydrant_35),
                    findViewById(R.id.hydrant_36),
                    findViewById(R.id.hydrant_37),
                    findViewById(R.id.hydrant_38),
                    findViewById(R.id.hydrant_39)
                )
            )

            criminals = arrayOf(
                arrayOf(
                    findViewById(R.id.criminal_00),
                    findViewById(R.id.criminal_01),
                    findViewById(R.id.criminal_02),
                    findViewById(R.id.criminal_03),
                    findViewById(R.id.criminal_04),
                    findViewById(R.id.criminal_05),
                    findViewById(R.id.criminal_06),
                    findViewById(R.id.criminal_07)
                ),
                arrayOf(
                    findViewById(R.id.criminal_08),
                    findViewById(R.id.criminal_09),
                    findViewById(R.id.criminal_10),
                    findViewById(R.id.criminal_11),
                    findViewById(R.id.criminal_12),
                    findViewById(R.id.criminal_13),
                    findViewById(R.id.criminal_14),
                    findViewById(R.id.criminal_15)
                ),
                arrayOf(
                    findViewById(R.id.criminal_16),
                    findViewById(R.id.criminal_17),
                    findViewById(R.id.criminal_18),
                    findViewById(R.id.criminal_19),
                    findViewById(R.id.criminal_20),
                    findViewById(R.id.criminal_21),
                    findViewById(R.id.criminal_22),
                    findViewById(R.id.criminal_23)
                ),
                arrayOf(
                    findViewById(R.id.criminal_24),
                    findViewById(R.id.criminal_25),
                    findViewById(R.id.criminal_26),
                    findViewById(R.id.criminal_27),
                    findViewById(R.id.criminal_28),
                    findViewById(R.id.criminal_29),
                    findViewById(R.id.criminal_30),
                    findViewById(R.id.criminal_31)
                ),
                arrayOf(
                    findViewById(R.id.criminal_32),
                    findViewById(R.id.criminal_33),
                    findViewById(R.id.criminal_34),
                    findViewById(R.id.criminal_35),
                    findViewById(R.id.criminal_36),
                    findViewById(R.id.criminal_37),
                    findViewById(R.id.criminal_38),
                    findViewById(R.id.criminal_39)
                )
            )    }
}


