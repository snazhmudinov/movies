package com.snazhmudinov.movies.modules

import android.content.Context
import com.snazhmudinov.movies.manager.MovieManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by snazhmudinov on 10/1/17.
 */
@Module
class MovieModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideDatabaseManager() = MovieManager(context)
}
