package com.snazhmudinov.movies.adapters

import android.content.Context
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.constans.Constants
import com.snazhmudinov.movies.models.Movie
import com.snazhmudinov.movies.modules.GlideApp
import kotlinx.android.synthetic.main.movie_rv_item_list.view.*
import java.text.DateFormatSymbols

/**
 * Created by snazhmudinov on 5/28/17.
 */

class MoviesAdapter(private val moviesList: List<Movie>, private val mContext: Context, private val isTablet: Boolean) : RecyclerView.Adapter<MoviesAdapter.MovieHolder>() {

    var movieInterface: MovieInterface? = null
    var indexOfSelectedMovie: Int = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    interface MovieInterface {
        fun onMovieSelected(movie: Movie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
        val inflater = LayoutInflater.from(mContext)
        val view: View = if (isTablet) {
            inflater.inflate(R.layout.movie_rv_item_list, parent, false)
        } else {
            inflater.inflate(R.layout.movie_rv_item_tile, parent, false)
        }

        return MovieHolder(view)
    }

    override fun onBindViewHolder(holder: MovieHolder, position: Int) {
        val currentMovie = moviesList[position]
        val uri = if (currentMovie.savedFilePath == null) { Uri.parse(Constants.POSTER_BASE_URL + currentMovie.posterPath) }
                    else { Uri.parse(currentMovie.savedFilePath) }

        GlideApp.with(mContext).load(uri).into(holder.itemView.poster)
        holder.itemView.movie_title?.text = currentMovie.originalTitle
        holder.itemView.additional_info?.text = formatMovieDate(currentMovie)

        val backgroundColor = if (position == indexOfSelectedMovie) ContextCompat.getColor(mContext, R.color.gray_selector)
                              else ContextCompat.getColor(mContext, android.R.color.transparent)
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount() = moviesList.size

    inner class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init { itemView.setOnClickListener { openMovieDetails() } }

        private fun openMovieDetails() {
            val movie = moviesList[adapterPosition]
            indexOfSelectedMovie = adapterPosition
            movieInterface?.onMovieSelected(movie)
        }
    }

    private fun formatMovieDate(movie: Movie): String {
        val movieDate = movie.releaseDate
        val dateComponents = movieDate.split("-")

        val dfs = DateFormatSymbols()

        val day = dateComponents[2]
        val month = dfs.months[dateComponents[1].toInt() - 1]
        val year = dateComponents[0]

        return "$month $day, $year"
    }
}
