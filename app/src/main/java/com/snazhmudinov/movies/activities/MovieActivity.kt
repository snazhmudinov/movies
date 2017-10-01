package com.snazhmudinov.movies.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.fragments.MovieFragment
import com.snazhmudinov.movies.models.Movie

/**
 * Created by snazhmudinov on 6/10/17.
 */

class MovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.movie_fragment, createMovieFragment())
                    .commit()
        }
    }

    private fun createMovieFragment(): MovieFragment {
        val movie = intent.getParcelableExtra<Movie>(Constants.MOVIE_KEY)
        val isLocalPoster = intent.getBooleanExtra(Constants.LOCAL_POSTER, false)

        return MovieFragment.newInstance(movie, isLocalPoster)
    }
}