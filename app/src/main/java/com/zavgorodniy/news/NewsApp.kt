package com.zavgorodniy.news

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class NewsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initDB()
    }

    private fun initDB() {

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .build()

        Realm.setDefaultConfiguration(config)
    }
}