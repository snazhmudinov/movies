package com.snazhmudinov.movies.manager

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
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
fun downloadImageAndGetPath(context: Context, movie: Movie, downloadInterface: DownloadInterface) {

    Picasso.with(context).load(movie.webPosterPath).into(object: Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            val bytes = ByteArrayOutputStream()

            if (bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes) ?: false) {
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", null)
                movie.posterPath = path
                downloadInterface.downloadFinished()
            }
        }
    })
}

private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    } finally {
        if (cursor != null) {
            cursor.close()
        }
    }
}