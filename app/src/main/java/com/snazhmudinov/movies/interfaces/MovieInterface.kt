package com.snazhmudinov.movies.interfaces

import com.snazhmudinov.movies.models.Movie


/**
 * Created by snazhmudinov on 10/1/17.
 */
interface MovieInterface {
    fun onMovieSelected(movie: Movie?, isLocalImage: Boolean)
}