package com.augniture.beta.ui

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.augniture.beta.R
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.ui.auth.signin.SignInFragment
import com.augniture.beta.ui.auth.AuthViewModel
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import kotlinx.android.synthetic.main.activity_auth.*
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.databinding.ActivityAuthBinding
import com.augniture.beta.framework.analytics.AmplitudeAnalyticsCons
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.auth.register.RegisterFragment

class AuthActivity : AppCompatActivity() {

    private var _authActivityBinding: ActivityAuthBinding? = null
    val authActivityBinding get() = _authActivityBinding!!

    private lateinit var authViewModel: AuthViewModel

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var amplitudeInstance: AmplitudeClient

    private var networkManager: NetworkManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _authActivityBinding = ActivityAuthBinding.inflate(layoutInflater)
        val authActivityView = authActivityBinding.root

        setContentView(authActivityView)

        // NetworkManager (Broadcast Receiver for Network changes)
        networkManager = NetworkManager(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)

        registerReceiver(networkManager, intentFilter)

        amplitudeInstance = Amplitude
            .getInstance()
            .initialize(this, getString(R.string.amplitude_ak))
            .enableForegroundTracking(application)

        // Registro: tiempo de sesion
        amplitudeInstance.trackSessionEvents(true)

        // Registro: abre app
        amplitudeInstance.logEvent(AmplitudeAnalyticsCons.OPEN_APP)

        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        authViewModel = ViewModelProviders.of(this, AugnitureViewModelFactory).get(
            AuthViewModel::class.java)

        window.statusBarColor = Color.BLACK

        setupGoToSignInGoToRegisterButtonListeners()
    }

    override fun onStart() {
        super.onStart()

        val currentUsuarioId = sharedPreferences.getString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID, "")

        if (currentUsuarioId != "") {
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            startActivity(mainActivityIntent)
        }
    }

    override fun onStop() {
        super.onStop()

        Log.i("AuthActivity: ", "AuthActivity onStop()")

        _authActivityBinding = null

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (networkManager != null) { unregisterReceiver(networkManager) }
    }

    private fun setupGoToSignInGoToRegisterButtonListeners() {

        goToSignInBtn.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentAuth, SignInFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        goToRegisterBtn.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentAuth, RegisterFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

    }

}
