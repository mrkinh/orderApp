package com.homemade.ordapp

import android.app.Application

class SOrdApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}