package com.snazhmudinov.movies.modules

import com.snazhmudinov.movies.constans.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by snazhmudinov on 10/1/17.
 */
@Module
class NetworkModule {

    @Provides @Singleton fun provideRetrofit() = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}