package com.example.week2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.week2.logic.GameManager
import com.example.week2.utilities.SignalManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_FAB_left: ExtendedFloatingActionButton
    private lateinit var main_FAB_right: ExtendedFloatingActionButton
    private lateinit var main_IMG_cars: Array<AppCompatImageView>
    private lateinit var hydrants: Array<Array<ImageView>>

    private lateinit var gameManager: GameManager

    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SignalManager.init(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViews()
        initGame()
        initViews()

        findViewById<View>(R.id.main).post {
            if (main_IMG_cars.all { it.height > 0 && it.width > 0 }) {
                startGameLoop()
            } else {
                Toast.makeText(this, "שגיאה: לא הצלחנו להתחיל את המשחק", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initGame() {
        gameManager = GameManager(main_IMG_cars, hydrants)
    }

    private fun initViews() {
        main_FAB_left.setOnClickListener {
            gameManager.move(GameManager.LEFT)
        }

        main_FAB_right.setOnClickListener {
            gameManager.move(GameManager.RIGHT)
        }
    }

    private fun startGameLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val hits = gameManager.updateHydrants()
                if (hits.isNotEmpty()) {
                    val alive = gameManager.onHit()
                    updateHearts()
                    SignalManager
                        .getInstance()
                        .toast("התנגשות!!")
                    SignalManager
                        .getInstance()
                        .vibrate()
                    if (!alive) {
                        SignalManager
                            .getInstance()
                            .toast("Game Over!")

                        handler.removeCallbacksAndMessages(null)
                        return
                    }
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
            findViewById(R.id.main_IMG_car2)
        )

        hydrants = arrayOf(
            arrayOf(
                findViewById(R.id.hydrant_0),
                findViewById(R.id.hydrant_1),
                findViewById(R.id.hydrant_2),
                findViewById(R.id.hydrant_3)
            ),
            arrayOf(
                findViewById(R.id.hydrant_4),
                findViewById(R.id.hydrant_5),
                findViewById(R.id.hydrant_6),
                findViewById(R.id.hydrant_7)
            ),
            arrayOf(
                findViewById(R.id.hydrant_8),
                findViewById(R.id.hydrant_9),
                findViewById(R.id.hydrant_10),
                findViewById(R.id.hydrant_11)
            )
        )
    }
}
