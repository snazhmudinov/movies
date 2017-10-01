package com.snazhmudinov.movies.fragments

import android.app.Activity
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
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.CastAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.database.DatabaseManager
import com.snazhmudinov.movies.manager.DownloadInterface
import com.snazhmudinov.movies.manager.MovieManager
import com.snazhmudinov.movies.manager.deleteImageFromMediaStore
import com.snazhmudinov.movies.manager.downloadImageAndGetPath
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.movie_content.*
import kotlinx.android.synthetic.main.movie_fragment.*
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/23/17.
 */
class MovieFragment: Fragment(), View.OnClickListener, DownloadInterface {

    @Inject lateinit var mMovieManager: MovieManager
    @Inject lateinit var mDatabaseManager: DatabaseManager

    var movie: Movie? = null
    private var isLocalPoster = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity.application as MovieApplication).appComponents.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater?.inflate(R.layout.movie_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movie = activity.intent.getParcelableExtra(Constants.MOVIE_KEY)
        isLocalPoster = activity.intent.getBooleanExtra(Constants.LOCAL_POSTER, false)

        movie?.let {
            toolbar_layout.title = it.originalTitle

            val posterPath = if (isLocalPoster) Uri.parse(it.savedFilePath) else it.webPosterPath
            poster_container.setImageURI(posterPath)

            setFocusCropRect()
            mMovieManager.getCast(it) { list -> setupMovieCast(list) }
            configureToolbar()
            configureFab(mDatabaseManager.isMovieInDatabase(it))
        }

        fab?.setOnClickListener(this)
        trailer_icon?.setOnClickListener(this)
        actors_drop_down?.setOnClickListener(this)

    }

    private fun setupMovieCast(castList : List<Cast>) {
        cast_recycler_view.layoutManager = LinearLayoutManager(context)
        val castAdapter = CastAdapter(castList, context)
        cast_recycler_view.adapter = castAdapter
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
        movie_toolbar.setNavigationOnClickListener {
            activity.finish()
        }
    }

    private fun configureFab(isAdded: Boolean) {
        val resId =  if(isAdded) R.drawable.ic_clear else R.drawable.ic_add
        fab.setImageResource(resId)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab -> {
                movie?.let {
                    if (mDatabaseManager.isMovieInDatabase(it)) {
                        val localImgPath = it.savedFilePath ?: ""
                        if (localImgPath.isNotEmpty()) {
                            if (deleteImageFromMediaStore(context, localImgPath)) {
                                mDatabaseManager.deleteMovieFromDb(it)
                                configureFab(mDatabaseManager.isMovieInDatabase(it))

                                if (isLocalPoster) {
                                    val intent = Intent()
                                    intent.putExtra(Constants.MOVIE_TO_DELETE, it)
                                    activity.setResult(Activity.RESULT_OK, intent)
                                    activity.finish()
                                }
                            }
                        }
                    } else {
                        downloadImageAndGetPath(context, it, this)
                    }
                }
            }

            R.id.trailer_icon -> {
                movie?.let {
                    mMovieManager.getTrailer(it) {trailer ->
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(trailer)))}
                }
            }

            R.id.actors_drop_down -> {
                val visibility = cast_recycler_view.visibility
                cast_recycler_view.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE

                val drawable = if (cast_recycler_view.visibility == View.VISIBLE) R.drawable.ic_arrow_drop_up
                else R.drawable.ic_arrow_drop_down
                actors_drop_down.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
            }
        }
    }

    override fun downloadFinished() {
        context.runOnUiThread {
            movie?.let {
                mDatabaseManager.insertMovieIntoDB(it)
                configureFab(mDatabaseManager.isMovieInDatabase(it))
                displaySnackbar()
            }
        }
    }

    private fun setFocusCropRect() {
        val point = PointF(0.5f, 0f)
        poster_container.hierarchy.setActualImageFocusPoint(point)
    }
}