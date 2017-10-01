package com.snazhmudinov.movies.components

import com.snazhmudinov.movies.fragments.MovieFragment
import com.snazhmudinov.movies.fragments.MoviesListFragment
import com.snazhmudinov.movies.modules.DatabaseModule
import com.snazhmudinov.movies.modules.MovieModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by snazhmudinov on 10/1/17.
 */
@Singleton
@Component(modules = arrayOf(DatabaseModule::class, MovieModule::class))
interface AppComponents {
    fun inject(o: MoviesListFragment)
    fun inject(o: MovieFragment)
}