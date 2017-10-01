package com.snazhmudinov.movies.manager

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.snazhmudinov.movies.models.Movie
import okhttp3.*
import java.io.File
import java.io.IOException

/**
 * Created by snazhmudinov on 7/25/17.
 */

interface DownloadInterface {
    fun downloadFinished()
}

fun downloadImageAndGetPath(context: Context, movie: Movie, downloadInterface: DownloadInterface) {
    val client = OkHttpClient()
    val request = Request.Builder()
            .url(movie.webPosterPath.toString())
            .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call?, response: Response?) {
            if (response?.isSuccessful ?: false) {
                response?.let {
                    val bytes = it.body()?.bytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size ?: 0)
                    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "", null)
                    movie.savedFilePath = path
                    downloadInterface.downloadFinished()
                }
            }
        }

        override fun onFailure(call: Call?, e: IOException?) {
            e?.printStackTrace()
        }
    })
}

fun deleteImageFromMediaStore(context: Context, path: String): Boolean {
    val fileToDelete = File(getRealPathFromURI(context, Uri.parse(path)))

    if (fileToDelete.exists()) {
        return fileToDelete.delete()
    }

    return false
}

private fun getRealPathFromURI(context: Context, contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    } finally {
        if (cursor != null) {
            cursor.close()
        }
    }
}