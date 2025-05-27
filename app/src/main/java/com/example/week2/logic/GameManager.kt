package com.example.week2.logic

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

class GameManager(
    private val cars: Array<AppCompatImageView>,
    private val hydrants: Array<Array<ImageView>>,
    private val criminals: Array<Array<ImageView>>
) {

    companion object {
        const val LEFT = -1
        const val RIGHT = 1
        const val LANES = 5
        private const val MAX_LIVES = 3
        private const val ROWS = 8
        private const val FRAME_INTERVAL_HYDRANTS = 2
        private const val FRAME_INTERVAL_CRIMINALS = 4
    }

    private var currentLane: Int = LANES / 2
    private var lives: Int = MAX_LIVES
    private var frameCount = 0
    private var score = 0

    init {
        showOnlyCurrentCar()
        resetHydrants()
        resetCriminals()
    }

    fun move(direction: Int) {
        val newLane = currentLane + direction
        if (newLane in 0 until LANES) {
            currentLane = newLane
            showOnlyCurrentCar()
        }
    }

    fun moveLeft() {
        move(LEFT)
    }

    fun moveRight() {
        move(RIGHT)
    }

    fun getCurrentLane(): Int = currentLane
    fun getLives(): Int = lives
    fun getScore(): Int = score

    fun onHit(): Boolean {
        lives--
        return lives > 0
    }

    private fun showOnlyCurrentCar() {
        cars.forEachIndexed { index, car ->
            car.visibility = if (index == currentLane) View.VISIBLE else View.INVISIBLE
        }
    }

    fun resetHydrants() {
        for (lane in 0 until LANES) {
            for (row in 0 until ROWS) {
                hydrants[lane][row].visibility = View.INVISIBLE
            }
        }
    }

    fun resetCriminals() {
        for (lane in 0 until LANES) {
            for (row in 0 until ROWS) {
                criminals[lane][row].visibility = View.INVISIBLE
            }
        }
    }

    fun updateHydrants(): List<Pair<ImageView, Int>> {
        frameCount++
        val hits = mutableListOf<Pair<ImageView, Int>>()

        for (lane in 0 until LANES) {
            for (row in ROWS - 1 downTo 1) {
                hydrants[lane][row].visibility = hydrants[lane][row - 1].visibility
            }
            hydrants[lane][0].visibility = View.INVISIBLE
        }

        if (frameCount % FRAME_INTERVAL_HYDRANTS == 0) {
            val availableLanes = (0 until LANES).filter {
                hydrants[it][0].visibility == View.INVISIBLE &&
                        criminals[it][0].visibility == View.INVISIBLE
            }
            if (availableLanes.isNotEmpty()) {
                val randomLane = availableLanes.random()
                hydrants[randomLane][0].visibility = View.VISIBLE
            }
        }

        for (lane in 0 until LANES) {
            if (hydrants[lane][ROWS - 1].visibility == View.VISIBLE && lane == currentLane) {
                hits.add(hydrants[lane][ROWS - 1] to lane)
                hydrants[lane][ROWS - 1].visibility = View.INVISIBLE
            }
        }

        return hits
    }

    fun updateCriminals(): Boolean {
        var scored = false

        for (lane in 0 until LANES) {
            for (row in ROWS - 1 downTo 1) {
                criminals[lane][row].visibility = criminals[lane][row - 1].visibility
            }
            criminals[lane][0].visibility = View.INVISIBLE
        }

        if (frameCount % FRAME_INTERVAL_CRIMINALS == 0) {
            val availableLanes = (0 until LANES).filter {
                criminals[it][0].visibility == View.INVISIBLE &&
                        hydrants[it][0].visibility == View.INVISIBLE
            }
            if (availableLanes.isNotEmpty()) {
                val randomLane = availableLanes.random()
                criminals[randomLane][0].visibility = View.VISIBLE
            }
        }

        for (lane in 0 until LANES) {
            if (criminals[lane][ROWS - 1].visibility == View.VISIBLE && lane == currentLane) {
                criminals[lane][ROWS - 1].visibility = View.INVISIBLE
                score += 10
                scored = true
            }
        }

        return scored
    }
}
