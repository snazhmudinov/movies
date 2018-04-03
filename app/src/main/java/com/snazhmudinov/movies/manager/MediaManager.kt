package com.snazhmudinov.movies.manager

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.snazhmudinov.movies.models.Movie
import okhttp3.*
import java.io.IOException

/**
 * Created by snazhmudinov on 7/25/17.
 */

fun downloadImageAndGetPath(context: Context, movie: Movie, downloadFinished: () -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
            .url(movie.webPosterPath.toString())
            .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call?, response: Response?) {
            if (response?.isSuccessful == true) {
                val bytes = response.body()?.bytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", null)
                movie.savedFilePath = path
                downloadFinished()
            }
        }

        override fun onFailure(call: Call?, e: IOException?) {
            e?.printStackTrace()
        }
    })
}

fun deleteImageFromMediaStore(context: Context, path: String) {
    context.contentResolver.delete(Uri.parse(path), null, null)
}