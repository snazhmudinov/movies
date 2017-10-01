package com.snazhmudinov.movies.adapters

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.snazhmudinov.movies.R
import com.snazhmudinov.movies.models.Cast
import kotlinx.android.synthetic.main.actor_rv_item.view.*

/**
 * Created by snazhmudinov on 6/17/17.
 */
class CastAdapter(val castList : List<Cast>, val context: Context) : RecyclerView.Adapter<CastHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CastHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.actor_rv_item, parent, false)

        return CastHolder(view)
    }

    override fun getItemCount() = castList.size

    override fun onBindViewHolder(holder: CastHolder?, position: Int) {
        val cast = castList[position]
        holder?.actorName?.text = "${cast.name} as ${cast.character}"
        holder?.actorPhoto?.setImageURI(Uri.parse(cast.profilePath))
    }
}

class CastHolder(item:View) : RecyclerView.ViewHolder(item) {
    var actorPhoto: SimpleDraweeView = item.actor_photo
    var actorName: TextView = item.actor_name
}
