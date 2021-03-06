package com.snazhmudinov.movies.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.snazhmudinov.movies.MovieListInterface
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.CastAdapter
import com.snazhmudinov.movies.adapters.TrailersAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.database.DatabaseManager
import com.snazhmudinov.movies.extensions.openPermissionScreen
import com.snazhmudinov.movies.manager.MovieManager
import com.snazhmudinov.movies.manager.deleteImageFromMediaStore
import com.snazhmudinov.movies.manager.downloadImageAndGetPath
import com.snazhmudinov.movies.models.Cast
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.modules.GlideApp
import kotlinx.android.synthetic.main.movie_content.*
import kotlinx.android.synthetic.main.movie_fragment.*
import kotlinx.android.synthetic.main.rating_view.*
import org.jetbrains.anko.runOnUiThread
import java.lang.ref.WeakReference
import javax.inject.Inject

/**
 * Created by snazhmudinov on 7/23/17.
 */
class MovieFragment: Fragment(), View.OnClickListener, TrailersAdapter.TrailerInterface {

    @Inject lateinit var mMovieManager: MovieManager
    @Inject lateinit var mDatabaseManager: DatabaseManager

    companion object {
        private const val WRITE_PERMISSION_REQUEST = 999

        fun newInstance(movie: Movie, isFavorite: Boolean): MovieFragment {
            val fragment = MovieFragment()
            fragment.movie = movie
            fragment.isFavoriteCategory = isFavorite
            return fragment
        }
    }

    private var movie: Movie? = null
    private var isFavoriteCategory = false
    private var movieListListener: WeakReference<MovieListInterface>? = null
    private var contextRef: WeakReference<Context>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as MovieApplication).appComponents.inject(this)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        movieListListener = WeakReference<MovieListInterface>(context as? MovieListInterface)
        context?.let { ctx -> contextRef = WeakReference(ctx) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.movie_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (movie == null) {

            movie = activity?.intent?.getParcelableExtra(Constants.MOVIE_KEY)
            isFavoriteCategory = activity?.intent?.getBooleanExtra(Constants.FAVORITE_KEY, false) ?: false

            movie?.let { movie ->
                if (movie.savedFilePath == null && mDatabaseManager.isMovieInDatabase(movie)) {
                    movie.savedFilePath = mDatabaseManager.getAllRecords().first { movie.id == it.id }.savedFilePath
                }
            }
        }

        movie?.let {
            toolbar_layout?.title = it.originalTitle

            val posterPath = if (isFavoriteCategory) {
                Uri.parse(it.savedFilePath)
            } else it.webPosterPath

            contextRef?.get()?.let { context ->
                GlideApp.with(context)
                        .load(posterPath)
                        .centerCrop()
                        .into(poster_container)
            }

            mMovieManager.getCast(it) { list ->
                setupMovieCast(list)

                //Set movie overview only after the actors were fetched
                movie_description?.text = it.overview
                average_vote?.text = getString(R.string.rating_placeholder, it.averageVote)
                overview_rating_container?.visibility = View.VISIBLE
            }
            configureToolbar()
            configureFab(mDatabaseManager.isMovieInDatabase(it))

            mMovieManager.getTrailer(it) { trailer ->
                trailers_recycler_view?.visibility = if (trailer.results?.isEmpty() == true) View.GONE else View.VISIBLE
                trailers_title?.visibility = if (trailer.results?.isEmpty() == true) View.GONE else View.VISIBLE

                val trailersAdapter = trailer.results?.let { data ->
                    TrailersAdapter(data, contextRef?.get() ?: return@getTrailer)
                }
                trailers_recycler_view?.layoutManager = LinearLayoutManager(contextRef?.get(), LinearLayoutManager.HORIZONTAL, false)
                trailers_recycler_view?.adapter = trailersAdapter
                trailersAdapter?.trailerListener = this
            }
        }

        fab?.setOnClickListener(this)
        actors_drop_down?.setOnClickListener(this)
    }

    override fun playTrailer(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun setupMovieCast(castList : List<Cast>) {
        val castAdapter = CastAdapter(castList, contextRef?.get() ?: return)

        cast_recycler_view?.apply {
            layoutManager = LinearLayoutManager(contextRef?.get())
            adapter = castAdapter
        }
    }

    private fun displaySnackbar() {
        movie?.let { movie ->
            val snackbar = Snackbar.make(view!!, R.string.added_to_favorites, Snackbar.LENGTH_LONG)
            snackbar.setAction(R.string.undo) {
                deleteMovieFromDB(movie)
                configureFab(mDatabaseManager.isMovieInDatabase(movie))
            }
            snackbar.show()
        }
    }

    private fun configureToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(movie_toolbar)
        movie_toolbar?.setNavigationOnClickListener {
            activity?.finish()
        }
    }

    private fun configureFab(isAdded: Boolean) {
        val resId =  if(isAdded) R.drawable.ic_clear else R.drawable.ic_add
        fab?.setImageResource(resId)
    }

    override fun onClick(v: View?) {
        val context = contextRef?.get() ?: return

        when(v?.id) {
            R.id.fab -> {
                movie?.let {
                    if (mDatabaseManager.isMovieInDatabase(it)) {
                            deleteMovieFromDB(it)

                            if (isFavoriteCategory) {
                                val intent = Intent()
                                intent.putExtra(Constants.MOVIE_TO_DELETE, it)
                                activity?.setResult(Activity.RESULT_OK, intent)
                                if (movieListListener?.get()?.isMasterPaneMode() == true) {
                                    movieListListener?.get()?.onDeleteMovie(it)
                                } else {
                                    activity?.finish()
                                }
                            }
                    } else {
                        if (Connectivity.isNetworkAvailable(context)) {
                            saveMovieToDB(it)
                        } else {
                            Connectivity.showNoNetworkToast(context)
                        }
                    }
                }
            }

            R.id.actors_drop_down -> {
                val visibility = cast_recycler_view.visibility
                cast_recycler_view.visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE

                val drawable = if (cast_recycler_view.visibility == View.VISIBLE) R.drawable.icon_hide
                else R.drawable.icon_show
                actors_drop_down.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)
            }
        }
    }

    private fun deleteMovieFromDB(movie: Movie) {
        val context = contextRef?.get() ?: return

        movie.savedFilePath?.let { path ->
            deleteImageFromMediaStore(context, path)
        }

        mDatabaseManager.deleteMovieFromDb(movie)
        configureFab(mDatabaseManager.isMovieInDatabase(movie))
    }

    private fun saveMovieToDB(movie: Movie) {
        val context = contextRef?.get() ?: return

        if (isWritePermissionGranted()) {
            context.runOnUiThread {
                downloadImageAndGetPath(context, movie) {
                    context.runOnUiThread {
                        mDatabaseManager.insertMovieIntoDB(movie)
                        configureFab(mDatabaseManager.isMovieInDatabase(movie))
                        displaySnackbar()
                    }
                }
            }

        } else {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionDialog()

            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_PERMISSION_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            WRITE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    movie?.let { saveMovieToDB(it) }
                } else {
                    Toast.makeText(activity, R.string.permission_denied_message, Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showPermissionDialog() {
        val context = contextRef?.get() ?: return

        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.permission_dialog_message)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                    context.openPermissionScreen()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }

        builder.create().show()
    }

    private fun isWritePermissionGranted() =
            contextRef?.get()?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } == PackageManager.PERMISSION_GRANTED
}