package com.example.movieproject


import android.app.Application
import com.example.movieproject.di.AppComponent
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApp : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
    }
}