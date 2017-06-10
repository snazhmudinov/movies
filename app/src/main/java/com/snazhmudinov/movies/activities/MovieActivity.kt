package com.snazhmudinov.movies.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.models.Movie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie.*
import org.parceler.Parcels

/**
 * Created by snazhmudinov on 6/10/17.
 */

class MovieActivity : AppCompatActivity() {

    var mIsAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movie : Movie = Parcels.unwrap(intent.getParcelableExtra(Constants.MOVIE_KEY))

        configureToolbar()
        configureFab()
        fab.setOnClickListener {
            mIsAdded = !mIsAdded
            configureFab()
        }

        toolbar_layout.title = movie.originalTitle
        Picasso.with(this)
                .load(Constants.POSTER_BASE_URL + movie.posterPath)
                .into(poster_container)
    }

    fun configureToolbar() {
        setSupportActionBar(movie_toolbar)
        movie_toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    fun configureFab() {
        val resId =  if(mIsAdded) R.drawable.ic_clear else R.drawable.ic_add
        fab.setImageResource(resId)
    }
}
