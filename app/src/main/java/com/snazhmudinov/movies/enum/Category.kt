package com.snazhmudinov.movies.enum

import com.snazhmudinov.movies.R

/**
 * Created by snazhmudinov on 8/8/17.
 */
enum class Category(val id: Int) {
    popular(R.id.action_popular),
    now_playing(R.id.action_now_playing),
    top_rated(R.id.action_top_rated),
    upcoming(R.id.action_upcoming),
    favorite(R.id.action_favorite)
}