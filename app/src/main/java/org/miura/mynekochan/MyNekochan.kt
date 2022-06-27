package org.miura.mynekochan

import android.app.Application

class MyNekochan : Application() {
    companion object {
        lateinit var instance: MyNekochan
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}