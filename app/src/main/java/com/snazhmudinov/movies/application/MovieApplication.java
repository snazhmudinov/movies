package com.snazhmudinov.movies.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.snazhmudinov.movies.components.DaggerNetworkComponents;
import com.snazhmudinov.movies.components.NetworkComponents;
import com.snazhmudinov.movies.modules.NetworkModule;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public class MovieApplication extends Application {

    private NetworkComponents networkComponents;

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
        networkComponents = DaggerNetworkComponents.builder()
                                .networkModule(new NetworkModule())
                                .build();
    }

    public NetworkComponents getNetworkComponents() {
        return networkComponents;
    }
}
