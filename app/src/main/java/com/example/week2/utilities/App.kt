package com.example.week2.utilities

import android.app.Application
import com.example.week2.R


class App: Application() {
    override fun onCreate() {
        super.onCreate()
        BackgroundMusicPlayer.init(this)
        BackgroundMusicPlayer.getInstance().setResourceId(R.raw.background_music)
    }

    override fun onTerminate() {
        super.onTerminate()
        BackgroundMusicPlayer.getInstance().stopMusic()

    }
}