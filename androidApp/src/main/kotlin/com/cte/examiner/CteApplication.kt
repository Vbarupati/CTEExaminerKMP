package com.cte.examiner

import android.app.Application
import com.cte.examiner.di.AppContainer
import com.russhwolf.settings.SharedPreferencesSettings

class CteApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("cte_prefs", MODE_PRIVATE)
        container = AppContainer(SharedPreferencesSettings(prefs))
    }
}