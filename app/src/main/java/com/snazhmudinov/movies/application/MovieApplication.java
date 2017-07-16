package com.snazhmudinov.movies.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.snazhmudinov.movies.components.AppComponents;
import com.snazhmudinov.movies.components.DaggerAppComponents;
import com.snazhmudinov.movies.modules.DatabaseModule;
import com.snazhmudinov.movies.modules.NetworkModule;
import io.fabric.sdk.android.Fabric;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public class MovieApplication extends Application {

    private AppComponents appComponents;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Fresco.initialize(this);
        appComponents = DaggerAppComponents.builder()
                                .networkModule(new NetworkModule())
                                .databaseModule(new DatabaseModule(getApplicationContext()))
                                .build();
    }

    public AppComponents getAppComponents() {
        return appComponents;
    }
}
