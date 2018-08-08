package com.snazhmudinov.movies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.models.Result
import com.snazhmudinov.movies.modules.GlideApp
import kotlinx.android.synthetic.main.trailer_view.view.*

/**
 * Created by snazhmudinov on 3/5/18.
 */
class TrailersAdapter(private val data: List<Result>, private val context: Context): RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder>() {

    interface TrailerInterface {
        fun playTrailer(url: String)
    }

    var trailerListener: TrailerInterface? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.trailer_view, parent, false)

        return TrailerViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) {
        holder.bindTrailer(data[position])
    }


    inner class TrailerViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                val url = data[adapterPosition].trailerURL
                trailerListener?.playTrailer(url)
            }
        }

        fun bindTrailer(trailer: Result) {
            GlideApp.with(view).load(trailer.trailerThumbnail).into( view.trailer_thumbnail)
        }
    }
}