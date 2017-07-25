package com.snazhmudinov.movies.database

import android.content.Context
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.models.Movie
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import java.util.ArrayList

/**
 * Created by snazhmudinov on 7/16/17.
 */
class DatabaseManager(val context: Context) {

    fun insertMovieIntoDB(movie: Movie) {
        context.database.use {
            insert(MoviesDatabaseHelper.TABLE_NAME,
                    MoviesDatabaseHelper.COLUMN_MOVIE_ID to movie.id,
                    MoviesDatabaseHelper.COLUMN_MOVIE_NAME to movie.originalTitle,
                    MoviesDatabaseHelper.COLUMN_TRAILER_LINK to movie.trailer,
                    MoviesDatabaseHelper.COLUMN_POSTER_LINK to movie.posterPath,
                    MoviesDatabaseHelper.COLUMN_OVERVIEW to movie.overview,
                    MoviesDatabaseHelper.COLUMN_RELEASE_DATE to movie.releaseDate,
                    MoviesDatabaseHelper.COLUMN_POPULARITY to movie.popularity,
                    MoviesDatabaseHelper.COLUMN_VOTE_COUNT to movie.voteCount)
        }
    }

    fun isMovieInDatabase(movie: Movie): Boolean {
        var isInDb: Boolean = false
        context.database.use {
            select(MoviesDatabaseHelper.TABLE_NAME)
                    .whereArgs("(${MoviesDatabaseHelper.COLUMN_MOVIE_ID} = ${movie.id})")
                    .exec {
                        isInDb =  count > 0
                    }
        }
        return isInDb
    }

    fun deleteMovieFromDb(movie: Movie) {
        context.database.use {
            val count = delete(MoviesDatabaseHelper.TABLE_NAME, "(${MoviesDatabaseHelper.COLUMN_MOVIE_ID} = ${movie.id})")
            val stringRes = if (count > 0) R.string.success_deleting_movie else R.string.failure_deleting_movie
            Toast.makeText(context, stringRes, Toast.LENGTH_SHORT).show()
        }
    }

    fun tableHasRecords() = context.database.use {
        select(MoviesDatabaseHelper.TABLE_NAME, "*").exec {
            moveToFirst()
        }
    }

    fun getAllRecords(): List<Movie>? {
        val movies: MutableList<Movie> = ArrayList()

        context.database.use {
            select(MoviesDatabaseHelper.TABLE_NAME, "*").exec {
                if (moveToFirst()) {

                    while (moveToNext()) {
                        val movieId = getInt(getColumnIndex(MoviesDatabaseHelper.COLUMN_MOVIE_ID))
                        val movieName = getString(getColumnIndex(MoviesDatabaseHelper.COLUMN_MOVIE_NAME))
                        val trailerLink = getString(getColumnIndex(MoviesDatabaseHelper.COLUMN_TRAILER_LINK))
                        val posterPath = getString(getColumnIndex(MoviesDatabaseHelper.COLUMN_POSTER_LINK))
                        val overview = getString(getColumnIndex(MoviesDatabaseHelper.COLUMN_OVERVIEW))
                        val releaseDate = getString(getColumnIndex(MoviesDatabaseHelper.COLUMN_RELEASE_DATE))
                        val popularity = getDouble(getColumnIndex(MoviesDatabaseHelper.COLUMN_POPULARITY))
                        val voteCount = getInt(getColumnIndex(MoviesDatabaseHelper.COLUMN_VOTE_COUNT))

                        val movie = Movie(posterPath, overview, releaseDate, movieId, movieName,
                                movieName, popularity, voteCount)
                        movie.trailer = trailerLink

                        movies.add(movie)
                    }
                }
            }
        }

        return movies
    }
}