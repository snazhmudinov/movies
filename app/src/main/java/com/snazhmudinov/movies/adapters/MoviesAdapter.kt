package com.snazhmudinov.movies.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.models.Movie
import kotlinx.android.synthetic.main.movie_rv_item.view.*
import kotlinx.android.synthetic.main.movie_rv_land_item.view.*

/**
 * Created by snazhmudinov on 5/28/17.
 */

class MoviesAdapter(private val moviesList: List<Movie>,
                    private val mContext: Context) : RecyclerView.Adapter<MoviesAdapter.MovieHolder>() {

    private var isLocalImage = false
    var movieInterface: MovieInterface? = null
    var mode = PORTRAIT_ORIENTATION

    companion object {
        val PORTRAIT_ORIENTATION = 0
        val LANDSCAPE_ORIENTATION = 1
    }

    interface MovieInterface {
        fun onMovieSelected(movie: Movie, isLocalImage: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = when(viewType) {
            LANDSCAPE_ORIENTATION -> inflater.inflate(R.layout.movie_rv_land_item, parent, false)
            else -> inflater.inflate(R.layout.movie_rv_item, parent, false)
        }

        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val currentMovie = moviesList[position]
        val uri = if (isLocalImage)
            Uri.parse(currentMovie.savedFilePath)
        else
            Uri.parse(Constants.POSTER_BASE_URL + currentMovie.posterPath)

        holder.itemView.movie_title?.text = currentMovie.originalTitle
        holder.itemView.poster?.setImageURI(uri)
        holder.itemView.poster_land?.setImageURI(uri)
    }

    override fun getItemCount() = moviesList.size

    override fun getItemViewType(position: Int) = mode

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.poster?.setOnClickListener {
                openMovieDetails()
            }
        }

        fun openMovieDetails() {
            val movie = moviesList[adapterPosition]
            movieInterface?.onMovieSelected(movie, isLocalImage)
        }
    }

    fun setLocalImage(value: Boolean) {
        isLocalImage = value
    }
}
