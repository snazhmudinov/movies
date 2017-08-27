package com.snazhmudinov.movies.fragments

import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.CastAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.CastList
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.Trailer
import kotlinx.android.synthetic.main.movie_content.*
import kotlinx.android.synthetic.main.movie_fragment.*
import org.parceler.Parcels
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/23/17.
 */
class MovieFragment: Fragment() {

    @Inject private lateinit var mRetrofit: Retrofit

    private var mIsAdded = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.movie_fragment, container, false)

        (activity.application as MovieApplication).networkComponents.inject(this)

        return root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie : Movie = Parcels.unwrap(activity.intent.getParcelableExtra(Constants.MOVIE_KEY))

        configureToolbar()
        configureFab()

        fab.setOnClickListener {
            displaySnackbar()
            mIsAdded = !mIsAdded
            configureFab()
        }

        trailer_icon.setOnClickListener {
            playTrailer(movie)
        }

        actors_drop_down.setOnClickListener {
            val visibility = cast_recycler_view.visibility
            cast_recycler_view.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE

            val drawable = if (cast_recycler_view.visibility == View.VISIBLE) R.drawable.ic_arrow_drop_up
            else R.drawable.ic_arrow_drop_down
            actors_drop_down.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
        }

        toolbar_layout.title = movie.originalTitle
        poster_container.setImageURI(Uri.parse(Constants.POSTER_BASE_URL + movie.posterPath))
        setFocusCropRect()
        getCast(movie)
    }

    fun setupMovieCast(castList : List<Cast>) {
        cast_recycler_view.layoutManager = LinearLayoutManager(context)
        val castAdapter = CastAdapter(castList, context)
        cast_recycler_view.adapter = castAdapter
    }

    private fun displaySnackbar() {
        val mSnackbar = if (!mIsAdded)
            Snackbar.make(parent_view, R.string.added_to_favorites, Snackbar.LENGTH_LONG) else null
        mSnackbar?.setAction(R.string.undo, {
            mIsAdded = false
            configureFab()
        })
        mSnackbar?.show()
    }

    private fun configureToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(movie_toolbar)
        movie_toolbar.setNavigationOnClickListener {
            activity.finish()
        }
    }

    private fun configureFab() {
        val resId =  if(mIsAdded) R.drawable.ic_clear else R.drawable.ic_add
        fab.setImageResource(resId)
    }

    private fun playTrailer(movie : Movie) {
        val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getYouTubeTrailer(movie.id.toString(), Constants.API_KEY)

        call.enqueue(object : retrofit2.Callback<Trailer> {
            override fun onResponse(call: Call<Trailer>, response: Response<Trailer>) {
                if (response.isSuccessful) {
                    val responseResults = response.body()?.results

                    if (responseResults?.isNotEmpty() as Boolean) {
                        responseResults.let {
                            val url = it[0]?.trailerURL
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        }
                    } else {
                        errorToast(R.string.no_trailer_error)
                    }

                } else {
                    errorToast(R.string.unsuccessful_response)
                }
            }

            override fun onFailure(call: Call<Trailer>, t: Throwable) {
                errorToast(R.string.error_call)
            }
        })
    }

    private fun getCast(movie : Movie) {
        val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getCastList(movie.id.toString(), Constants.API_KEY)

        call.enqueue(object : retrofit2.Callback<CastList> {
            override fun onResponse(call: Call<CastList>?, response: Response<CastList>) {
                if (response.isSuccessful) {
                    val actors = response.body()?.castList
                    if (actors?.isNotEmpty() as Boolean) {
                        actors.let { setupMovieCast(it.subList(0, 5)) }
                    }

                } else {
                    errorToast(R.string.unsuccessful_response)
                }
            }

            override fun onFailure(call: Call<CastList>?, t: Throwable?) {
                errorToast(R.string.error_call)
            }
        })
    }

    fun errorToast(message : Int)  { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }

    private fun setFocusCropRect() {
        val point = PointF(0.5f, 0f)
        poster_container.hierarchy.setActualImageFocusPoint(point)
    }
}