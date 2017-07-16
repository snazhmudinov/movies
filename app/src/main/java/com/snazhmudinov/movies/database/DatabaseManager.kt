package com.snazhmudinov.movies.database

import android.content.Context
import android.widget.Toast
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.models.Movie
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

/**
 * Created by snazhmudinov on 7/16/17.
 */
class DatabaseManager(val context: Context) {

    fun insertMovieIntoDB(movie: Movie) {
        context.database.use {
            insert(MoviesDatabaseHelper.TABLE_NAME,
                    MoviesDatabaseHelper.COLUMN_MOVIE_ID to movie.id,
                    MoviesDatabaseHelper.COLUMN_MOVIE_NAME to movie.originalTitle)
        }
    }

    fun isMovieInDatabase(movie: Movie): Boolean {
        var isInDb: Boolean = false
        context.database.use {
            select(MoviesDatabaseHelper.TABLE_NAME)
                    .whereArgs("(${MoviesDatabaseHelper.COLUMN_MOVIE_ID} = {movieId}) and " +
                            "(${MoviesDatabaseHelper.COLUMN_MOVIE_NAME} = {movieName})",
                            "movieId" to movie.id,
                            "movieName" to movie.originalTitle)
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
}