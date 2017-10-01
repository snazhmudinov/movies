package com.snazhmudinov.movies.modules

import android.content.Context
import com.snazhmudinov.movies.database.DatabaseManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by snazhmudinov on 7/16/17.
 */
@Module
class DatabaseModule(private val context: Context) {

    @Provides
    @Singleton
    internal fun provideDatabaseManager() = DatabaseManager(context)
}
