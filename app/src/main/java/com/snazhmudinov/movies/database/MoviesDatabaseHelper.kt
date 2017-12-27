package com.snazhmudinov.movies.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by snazhmudinov on 7/16/17.
 */
class MoviesDatabaseHelper(context: Context): ManagedSQLiteOpenHelper(context, "MoviesDB_0", null, 1) {

    companion object {

        val TABLE_NAME = "Movies"

        val COLUMN_MOVIE_ID = "movie_id"
        val COLUMN_MOVIE_NAME = "name"
        val COLUMN_SAVED_PATH = "saved_path"
        val COLUMN_TRAILER_LINK = "trailer_link"
        val COLUMN_POSTER_LINK = "poster_link"
        val COLUMN_OVERVIEW = "overview"
        val COLUMN_RELEASE_DATE = "release_date"
        val COLUMN_POPULARITY = "popularity"
        val COLUMN_AVERAGE_VOTE = "average_vote"
        val COLUMN_VOTE_COUNT = "vote_count"


        private var instance: MoviesDatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context): MoviesDatabaseHelper {
            if (instance == null) {
                instance = MoviesDatabaseHelper(context.applicationContext)
            }

            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.createTable(
                TABLE_NAME, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                COLUMN_MOVIE_ID to INTEGER,
                COLUMN_MOVIE_NAME to TEXT,
                COLUMN_SAVED_PATH to TEXT,
                COLUMN_TRAILER_LINK to TEXT,
                COLUMN_POSTER_LINK to TEXT,
                COLUMN_OVERVIEW to TEXT,
                COLUMN_RELEASE_DATE to TEXT,
                COLUMN_POPULARITY to REAL,
                COLUMN_AVERAGE_VOTE to REAL,
                COLUMN_VOTE_COUNT to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(TABLE_NAME, true)
    }
}

val Context.database: MoviesDatabaseHelper
    get() = MoviesDatabaseHelper.getInstance(applicationContext)