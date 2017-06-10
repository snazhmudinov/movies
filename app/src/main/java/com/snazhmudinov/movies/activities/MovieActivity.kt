package com.snazhmudinov.movies.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.activity_movie.*
import org.parceler.Parcels

/**
 * Created by snazhmudinov on 6/10/17.
 */

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movie : Movie = Parcels.unwrap(intent.getParcelableExtra(Constants.MOVIE_KEY))

        configureToolbar()

        movie_toolbar.title = movie.originalTitle
    }

    fun configureToolbar() {
        setSupportActionBar(movie_toolbar)
        movie_toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
