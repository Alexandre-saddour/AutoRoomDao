package com.asaddour.autoroomdao

import android.app.Application

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppDatabase.init(this)
    }
}