package com.snazhmudinov.movies.application

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.snazhmudinov.movies.components.AppComponents
import com.snazhmudinov.movies.components.DaggerAppComponents
import com.snazhmudinov.movies.modules.DatabaseModule
import com.snazhmudinov.movies.modules.NetworkModule
import io.fabric.sdk.android.Fabric



/**
 * Created by snazhmudinov on 10/1/17.
 */
class MovieApplication: Application() {

    val appComponents: AppComponents by lazy {
        DaggerAppComponents
                .builder()
                .networkModule(NetworkModule())
                .databaseModule(DatabaseModule(applicationContext))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Fresco.initialize(this)
    }
}