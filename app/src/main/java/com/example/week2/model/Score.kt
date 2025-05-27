package com.example.week2.model

import java.io.Serializable

data class Score(
    val name: String,
    val value: Int,
    val latitude: Double,
    val longitude: Double
) : Serializable
