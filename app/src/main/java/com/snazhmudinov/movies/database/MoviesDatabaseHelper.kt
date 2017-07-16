package com.snazhmudinov.movies.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by snazhmudinov on 7/16/17.
 */
class MoviesDatabaseHelper(context: Context): ManagedSQLiteOpenHelper(context, "MoviesDB", null, 1) {

    companion object {

        val TABLE_NAME = "Movies"

        val COLUMN_MOVIE_ID = "movie_id"
        val COLUMN_MOVIE_NAME = "name"

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
                COLUMN_MOVIE_NAME to TEXT
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.dropTable(TABLE_NAME, true)
    }
}

val Context.database: MoviesDatabaseHelper
    get() = MoviesDatabaseHelper.getInstance(applicationContext)