package com.snazhmudinov.movies.components

import com.snazhmudinov.movies.fragments.BaseMovieFragment
import com.snazhmudinov.movies.modules.DatabaseModule
import com.snazhmudinov.movies.modules.NetworkModule
import dagger.Component
import javax.inject.Singleton

/**
 * Created by snazhmudinov on 10/1/17.
 */
@Singleton
@Component(modules = arrayOf(NetworkModule::class, DatabaseModule::class))
interface AppComponents {
    fun inject(o: BaseMovieFragment)
}