package com.snazhmudinov.movies.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PointF
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
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.adapters.CastAdapter
import com.snazhmudinov.movies.application.MovieApplication
import com.snazhmudinov.movies.connectivity.Connectivity
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.database.DatabaseManager
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
class MovieFragment: Fragment(), View.OnClickListener {

    @Inject lateinit var mMovieManager: MovieManager
    @Inject lateinit var mDatabaseManager: DatabaseManager

    companion object {
        private val WRITE_PERMISSION_REQUEST = 999
    }

    private var movie: Movie? = null
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

            mMovieManager.getTrailer(it) { trailer -> it.trailer = trailer }
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
                                deleteMovieFromDB(it)

                                if (isLocalPoster) {
                                    val intent = Intent()
                                    intent.putExtra(Constants.MOVIE_TO_DELETE, it)
                                    activity.setResult(Activity.RESULT_OK, intent)
                                    activity.finish()
                                }
                            }
                        } else {
                            deleteMovieFromDB(it)
                        }
                    } else {
                        if (Connectivity.isNetworkAvailable(activity)) {
                            saveMovieToDB(it)
                        } else {
                            Connectivity.showNoNetworkToast(activity)
                        }
                    }
                }
            }

            R.id.trailer_icon -> {
                movie?.let { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.trailer))) }
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

    private fun deleteMovieFromDB(movie: Movie) {
        mDatabaseManager.deleteMovieFromDb(movie)
        configureFab(mDatabaseManager.isMovieInDatabase(movie))
    }

    private fun setFocusCropRect() {
        val point = PointF(0.5f, 0f)
        poster_container.hierarchy.setActualImageFocusPoint(point)
    }

    private fun saveMovieToDB(movie: Movie) {
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

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
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
        val builder = AlertDialog.Builder(context)
                .setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.permission_dialog_message)
                .setPositiveButton(android.R.string.ok, { dialog, _ ->
                    dialog.dismiss()
                    context.openPermissionScreen()
                })
                .setNegativeButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss() })

        builder.create().show()
    }

    private fun isWritePermissionGranted() =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}