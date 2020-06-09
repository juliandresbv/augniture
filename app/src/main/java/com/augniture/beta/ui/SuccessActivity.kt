package com.augniture.beta.ui

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.augniture.beta.R
import kotlinx.android.synthetic.main.activity_success.*

class SuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)

        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        Handler().postDelayed({

            intentToMainActivity()

        }, 2500)

        goToMain.setOnClickListener {
            intentToMainActivity()
        }
    }

    override fun onStop() {
        super.onStop()

        finish()
    }

    private fun intentToMainActivity() {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(mainActivityIntent)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
