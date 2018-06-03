package com.snazhmudinov.movies.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.models.Cast
import kotlinx.android.synthetic.main.actor_rv_item.view.*

/**
 * Created by snazhmudinov on 6/17/17.
 */
class CastAdapter(val castList : List<Cast>, val context: Context) : RecyclerView.Adapter<CastHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.actor_rv_item, parent, false)

        return CastHolder(view)
    }

    override fun getItemCount() = castList.size

    override fun onBindViewHolder(holder: CastHolder, position: Int) {
        val cast = castList[position]
        holder.bindCast(cast)
    }
}

class CastHolder(private val item:View) : RecyclerView.ViewHolder(item) {

    fun bindCast(cast: Cast) {
        item.actor_photo.setImageURI(cast.profilePath, item.context)
        item.actor_name.text = "${cast.name} as ${cast.character}"
    }
}