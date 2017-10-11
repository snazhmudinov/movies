package com.snazhmudinov.movies.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by snazhmudinov on 10/11/17.
 */
class ConnectivityBroadcastReceiver: BroadcastReceiver() {

    interface NetworkListenerInterface {
        fun onNetworkStateChanged(isNetworkAvailable: Boolean)
    }

    lateinit var networkStateListener: NetworkListenerInterface

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            networkStateListener.onNetworkStateChanged(Connectivity.isNetworkAvailable(it))
        }
    }
}