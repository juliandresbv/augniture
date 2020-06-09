package com.augniture.beta.framework.network

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.augniture.beta.R
import com.augniture.beta.ui.AuthActivity
import com.augniture.beta.ui.MainActivity
import com.google.android.material.snackbar.Snackbar

class NetworkManager(
    val activity: Activity
) : BroadcastReceiver() {

    private var networkGlobalSnackbar: Snackbar? = null

    companion object {
        // Intent cons
        const val AIRPLANE_MODE_INTENT = Intent.ACTION_AIRPLANE_MODE_CHANGED
        const val NETWORK_INTENT = ConnectivityManager.CONNECTIVITY_ACTION

        // Snackbar cons
        const val NO_NETWORK = "NO_NETWORK"

        // Conn status cons
        const val INIT_CONN = -1
        const val AIRPLANE_ON = -2

        const val NO_INTERNET_CONN = 0

        const val WIFI_CONN = 1
        const val MOBILE_CONN = 2

        var CONN_STATUS: Int = INIT_CONN

        fun isOnline(context: Context): Boolean {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo

            //return (networkInfo != null && networkInfo.isConnected)
            return ( (CONN_STATUS == WIFI_CONN || CONN_STATUS == MOBILE_CONN) || (networkInfo != null && networkInfo.isConnected) )
        }

        fun isOnlineSimple(): Boolean {
            return ( (CONN_STATUS == WIFI_CONN || CONN_STATUS == MOBILE_CONN) )
        }

        fun makeSnackbar(view: View, text: CharSequence, duration: Int, type: String, context: Context): Snackbar? {
            var snackbar: Snackbar? = null

            if(type == NO_NETWORK) {
                snackbar = Snackbar.make(view, text, duration)
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.view.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.snackbarError, null))
                snackbar.setTextColor(Color.WHITE)
                snackbar.setAction("Cerrar") { snackbar.dismiss() }
            }

            return snackbar
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        //TODO("Not yet implemented")
        try {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connManager.activeNetworkInfo

            var activityView: View? = null


            when(activity) {
                is MainActivity -> {
                    activityView = activity.mainActivityBinding.root
                }
                is AuthActivity -> {
                    activityView = activity.authActivityBinding.root
                }
                else -> {
                    Log.i("NetworkManager: ", "Activity not associated to NetworkManager")
                }
            }

            if (networkInfo != null) {
                networkGlobalSnackbar?.dismiss()

                if(networkInfo?.type == ConnectivityManager.TYPE_WIFI &&
                    connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected) {
                    CONN_STATUS = WIFI_CONN

                    Log.i("NetworkManager: ", "Wi-Fi connection")
                    //Toast.makeText(context, "Wi-Fi connection", Toast.LENGTH_SHORT).show()
                } else if(networkInfo?.type == ConnectivityManager.TYPE_MOBILE &&
                        connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected) {
                    CONN_STATUS = MOBILE_CONN

                    Log.i("NetworkManager: ", "Mobile data connection")
                    //Toast.makeText(context, "Mobile data connection", Toast.LENGTH_SHORT).show()
                }
            } else {

                val isAirplaneModeOn =
                    ( (AIRPLANE_MODE_INTENT == intent.action.toString()) &&
                            (intent.getBooleanExtra("state", false)) )
                val isAirplaneModeOnSystem = Settings.System.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) == 0

                Log.i("Network Manager: ", "Action: ${intent.action}; Bool: ${(intent.getBooleanExtra("state", false))}; BoolSystem: $isAirplaneModeOnSystem")

                if (isAirplaneModeOn) {
                    CONN_STATUS = AIRPLANE_ON

                    Log.i("NetworkManager: ", "Airplane Mode On")
                    if (activityView != null) {
                        networkGlobalSnackbar = makeSnackbar(
                            activityView!!,
                            "No hay conexion a Internet.\n" +
                                    "El modo avión está activado.",
                            Snackbar.LENGTH_INDEFINITE,
                            NO_NETWORK,
                            context
                        )
                        networkGlobalSnackbar?.show()
                    }

                    //Toast.makeText(context, "Airplane mode is ON", Toast.LENGTH_SHORT).show()
                } else {
                    CONN_STATUS = NO_INTERNET_CONN

                    Log.i("NetworkManager: ", "No Internet connection")
                    if (activityView != null) {
                        networkGlobalSnackbar = makeSnackbar(
                            activityView!!,
                            "No hay conexion a Internet.\n" +
                                    "Algunas funciones estarán limitadas.",
                            Snackbar.LENGTH_INDEFINITE,
                            NO_NETWORK,
                            context
                        )
                        networkGlobalSnackbar?.show()
                    }

                    //Toast.makeText(context, "No Internet Network", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.d("Network Manager (Broadcast Receiver): ", e.toString())
        }

    }

}