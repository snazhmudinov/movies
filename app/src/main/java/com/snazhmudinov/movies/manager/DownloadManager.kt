package com.snazhmudinov.movies.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.MediaStore
import com.snazhmudinov.movies.models.Movie
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.io.*

/**
 * Created by snazhmudinov on 7/25/17.
 */

interface DownloadInterface {
    fun downloadFinished()
}
fun downloadImageAndGetPath(context: Context?, movie: Movie, downloadInterface: DownloadInterface) {

    Picasso.with(context).load(movie.poster).into(object: Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val bytes = ByteArrayOutputStream()

            if (bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes) ?: false) {
                val path = MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, "", null)
                movie.posterPath = path
                downloadInterface.downloadFinished()
            }
        }
    })
}