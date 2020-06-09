package com.augniture.beta.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.augniture.beta.R
import com.augniture.beta.databinding.ActivitySplashBinding
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants

class SplashActivity : AppCompatActivity() {

    private var _splashActivityBinding: ActivitySplashBinding? = null
    val splashActivityBinding get() = _splashActivityBinding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        makeFullScreen()

        _splashActivityBinding = ActivitySplashBinding.inflate(layoutInflater)
        var splashActivityView = splashActivityBinding.root

        setContentView(splashActivityView)

        Handler().postDelayed({

                // Start activity
                val authActivityIntent = Intent(this, AuthActivity::class.java)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(authActivityIntent)

                // Animate the loading of new activity
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }

    override fun onStart() {
        super.onStart()

        val currentUsuarioId = sharedPreferences.getString(
            SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID,
            SharedPreferencesConstants.VALOR_DEF_STRING
        )

        Log.i("SplashActivity: ", "SplashActivity onStart(): ID Usuario actual $currentUsuarioId")

        // Using a handler to delay loading the MainActivity
        /*
        Handler().postDelayed({


            if (currentUsuarioId != "") {
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(mainActivityIntent)

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            } else {
                // Start activity
                val authActivityIntent = Intent(this, AuthActivity::class.java)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(authActivityIntent)

                // Animate the loading of new activity
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

        }, 2000)
        */
    }

    override fun onStop() {
        super.onStop()

        // Close this activity
        Log.i("SplashActivity: ", "SplashActivity onStop()")

        _splashActivityBinding = null
        finish()
    }

    private fun makeFullScreen() {
        // Remove Title
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Make Fullscreen
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Hide the toolbar
        supportActionBar?.hide()
    }
}
