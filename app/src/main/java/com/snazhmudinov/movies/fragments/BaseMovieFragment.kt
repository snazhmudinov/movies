package com.snazhmudinov.movies.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.database.DatabaseManager
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by snazhmudinov on 8/27/17.
 */
open class BaseMovieFragment: Fragment() {

    @Inject protected lateinit var mRetrofit: Retrofit
    @Inject protected lateinit var mDatabaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as MovieApplication).networkComponents.inject(this)
    }
}