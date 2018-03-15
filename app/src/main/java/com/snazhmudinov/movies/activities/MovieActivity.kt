package com.snazhmudinov.movies.activities

import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snazhmudinov.movies.R

/**
 * Created by snazhmudinov on 6/10/17.
 */
private const val SMALLEST_WIDTH_TABLET = 600

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        newConfig?.let {
            if (it.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    it.smallestScreenWidthDp >= SMALLEST_WIDTH_TABLET) { finish() }
        }
    }
}