package com.snazhmudinov.movies.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

/**
 * Created by snazhmudinov on 10/8/17.
 */
object Connectivity {
    private var noNetworkToast: Toast? = null

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
    }

    fun showNoNetworkToast(context: Context) {
        noNetworkToast?.cancel()
        noNetworkToast = Toast.makeText(context, "Please make sure you have internet connection", Toast.LENGTH_SHORT)
        noNetworkToast?.show()
    }
}