package com.snazhmudinov.movies.modules;

import android.content.Context;

import com.snazhmudinov.movies.constans.Constants;
import com.snazhmudinov.movies.database.DatabaseManager;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by snazhmudinov on 5/28/17.
 */
@Module
public class NetworkModule {

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
