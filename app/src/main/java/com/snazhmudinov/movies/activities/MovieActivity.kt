package com.snazhmudinov.movies.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snazhmudinov.movies.R
import kotlinx.android.synthetic.main.activity_movie.*

/**
 * Created by snazhmudinov on 6/10/17.
 */

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        setSupportActionBar(movie_toolbar)
        movie_toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
