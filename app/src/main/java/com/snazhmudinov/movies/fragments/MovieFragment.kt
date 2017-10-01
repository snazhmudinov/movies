package com.snazhmudinov.movies.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.CastAdapter
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.endpoints.MoviesEndPointsInterface
import com.snazhmudinov.movies.manager.DownloadInterface
import com.snazhmudinov.movies.manager.deleteImageFromMediaStore
import com.snazhmudinov.movies.manager.downloadImageAndGetPath
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.CastList
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.models.Trailer
import kotlinx.android.synthetic.main.movie_fragment.*
import retrofit2.Call
import retrofit2.Response

/**
 * Created by snazhmudinov on 7/23/17.
 */
class MovieFragment: BaseMovieFragment(), View.OnClickListener, DownloadInterface {

    var movie: Movie? = null
    private var isLocalPoster = false

    companion object {
        val ARG_MOVIE = "ARG_MOVIE"
        val ARG_FLAG = "ARG_FLAG"

        fun newInstance(movie: Movie, isLocalPoster: Boolean): MovieFragment {
            val bundle = Bundle()
            bundle.putParcelable(ARG_MOVIE, movie)
            bundle.putBoolean(ARG_FLAG, isLocalPoster)
            val fragment = MovieFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.movie_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movie = arguments.getParcelable(ARG_MOVIE)
        isLocalPoster = arguments.getBoolean(ARG_FLAG, false)

        movie?.let {
            toolbar_layout?.title = it.originalTitle
            movie_name?.text = it.originalTitle

            val posterPath = if (isLocalPoster) Uri.parse(it.posterPath) else it.webPosterPath
            poster_container.setImageURI(posterPath)

            setFocusCropRect()
            getCast(it)
            configureToolbar()
            configureFab(mDatabaseManager.isMovieInDatabase(it))
        }

        fab?.setOnClickListener(this)
        trailer_icon?.setOnClickListener(this)
        actors_drop_down?.setOnClickListener(this)
    }

    fun setupMovieCast(castList : List<Cast>) {
        context?.let {
            val castAdapter = CastAdapter(castList, context)
            cast_recycler_view?.layoutManager = LinearLayoutManager(context)
            cast_recycler_view?.adapter = castAdapter
        }
    }

    private fun displaySnackbar() {
        movie?.let { movie ->
            val snackbar = Snackbar.make(view!!, R.string.added_to_favorites, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.undo) {
                mDatabaseManager.deleteMovieFromDb(movie)
                configureFab(mDatabaseManager.isMovieInDatabase(movie))
            }
            snackbar.show()
        }
    }

    private fun configureToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(movie_toolbar)
        movie_toolbar?.setNavigationOnClickListener {
            activity.finish()
        }
    }

    private fun configureFab(isAdded: Boolean) {
        val resId =  if(isAdded) R.drawable.ic_clear else R.drawable.ic_add
        fab.setImageResource(resId)
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

    private fun playTrailer(movie : Movie) {
        val service = mRetrofit.create(MoviesEndPointsInterface::class.java)
        val call = service.getYouTubeTrailer(movie.id.toString(), Constants.API_KEY)

        call.enqueue(object : retrofit2.Callback<Trailer> {
            override fun onResponse(call: Call<Trailer>, response: Response<Trailer>) {
                if (response.isSuccessful) {
                    val responseResults = response.body()?.results

                    if (responseResults?.isNotEmpty() as Boolean) {
                        responseResults.let {
                            val url = it[0].trailerURL
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab -> {
                movie?.let {
                    if (mDatabaseManager.isMovieInDatabase(it)) {
                        if (!isLocalPoster) { mDatabaseManager.adjustToLocalPoster(it) }
                        if (deleteImageFromMediaStore(context, it.posterPath)) {
                            mDatabaseManager.deleteMovieFromDb(it)
                            configureFab(mDatabaseManager.isMovieInDatabase(it))
                            //If we delete the movie and should land in fav categories
                            if (isLocalPoster) {
                                val intent = Intent()
                                intent.putExtra(Constants.MOVIE_TO_DELETE, it)
                                activity.setResult(Activity.RESULT_OK, intent)
                                activity.finish()
                            }
                        }
                    } else {
                        downloadImageAndGetPath(context, it, this)
                    }
                }
            }

            R.id.trailer_icon -> {
                movie?.let {
                    playTrailer(it)
                }
            }

            R.id.actors_drop_down -> {
                val visibility = cast_recycler_view?.visibility
                cast_recycler_view?.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE

                val drawable = if (cast_recycler_view?.visibility == View.VISIBLE) R.drawable.ic_arrow_drop_up
                else R.drawable.ic_arrow_drop_down
                actors_drop_down?.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
            }
        }
    }

    override fun downloadFinished() {
        movie?.let {
            mDatabaseManager.insertMovieIntoDB(it)
            configureFab(mDatabaseManager.isMovieInDatabase(it))
            displaySnackbar()
        }
    }

    fun errorToast(message : Int)  { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() }

    private fun setFocusCropRect() {
        val point = PointF(0.5f, 0f)
        poster_container.hierarchy.setActualImageFocusPoint(point)
    }
}