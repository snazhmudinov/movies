package com.snazhmudinov.movies

import com.snazhmudinov.movies.models.Movie

/**
 * Created by snazhmudinov on 1/21/18.
 */
interface MovieListInterface {
    fun isMasterPaneMode(): Boolean
    fun loadMovie(movie: Movie)
    fun showEmpty(show: Boolean)
    fun onDeleteMovie(movie: Movie)
}