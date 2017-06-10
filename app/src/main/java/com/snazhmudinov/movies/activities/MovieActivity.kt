package com.snazhmudinov.movies.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.Trailer
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie.*
import org.parceler.Parcels
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by snazhmudinov on 6/10/17.
 */

class MovieActivity : AppCompatActivity() {

    @Inject lateinit var mRetrofit:Retrofit

    var mIsAdded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        //Dependency injection
        val application = application as MovieApplication
        application.networkComponents.inject(this)

        val movie : Movie = Parcels.unwrap(intent.getParcelableExtra(Constants.MOVIE_KEY))

        configureToolbar()
        configureFab()

        fab.setOnClickListener {
            mIsAdded = !mIsAdded
            configureFab()
        }

        trailer_icon.setOnClickListener {
            playTrailer(movie)
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

    fun playTrailer(movie : Movie) {
        val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getYouTubeTrailer(movie.id.toString(), Constants.API_KEY)

        call.enqueue(object : retrofit2.Callback<Trailer> {
            override fun onResponse(call: Call<Trailer>, response: Response<Trailer>) {
                if (response.isSuccessful) {
                    val url = Constants.YOUTUBE_BASE_URL + response.body()?.results?.get(0)?.key

                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                } else {
                    Toast.makeText(this@MovieActivity, R.string.unsuccessful_response, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Trailer>, t: Throwable) {
                Toast.makeText(this@MovieActivity, R.string.error_call, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
