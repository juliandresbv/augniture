package com.augniture.beta.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.ViewModelProviders
import com.augniture.beta.R
import com.augniture.beta.databinding.ActivityMainBinding
import com.augniture.beta.databinding.ActivityUserProfileBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.ui.auth.AuthViewModel
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {

    private var _userProfileActivityBinding : ActivityUserProfileBinding? = null
    val userProfileActivityBinding get() = _userProfileActivityBinding!!

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var glideInstance: RequestManager

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _userProfileActivityBinding = ActivityUserProfileBinding.inflate(layoutInflater)
        val userProfileActivityView = userProfileActivityBinding.root

        setContentView(userProfileActivityView)

        sharedPreferences = getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        glideInstance = Glide.with(this)

        authViewModel = ViewModelProviders.of(this, AugnitureViewModelFactory).get(AuthViewModel::class.java)

        val usuarioActual = sharedPreferences.getString(
            SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID,
            SharedPreferencesConstants.VALOR_DEF_STRING
        )

        if (usuarioActual != "") {
            val usuarioFoto = sharedPreferences.getString(
                SharedPreferencesConstants.KEY_USUARIO_ACTUAL_FOTO,
                SharedPreferencesConstants.VALOR_DEF_STRING
            )

            val usuarioNombre = sharedPreferences.getString(
                SharedPreferencesConstants.KEY_USUARIO_ACTUAL_NOMBRE,
                SharedPreferencesConstants.VALOR_DEF_STRING
            )

            val usuarioEmail = sharedPreferences.getString(
                SharedPreferencesConstants.KEY_USUARIO_ACTUAL_EMAIL,
                SharedPreferencesConstants.VALOR_DEF_STRING
            )

            val userImageIV: ImageView = userProfileActivityBinding.userImageIV

            glideInstance
                .load(usuarioFoto)
                .error(glideInstance.load(R.drawable.ic_user))
                .apply(RequestOptions.circleCropTransform())
                .into(userImageIV)

            userNameTV.text = usuarioNombre
            userEmailTV.text = usuarioEmail
        }

        signOut.setOnClickListener {
            authViewModel.signOutUsuario()
            sharedPreferences.edit().clear().apply()

            val authActivityIntent = Intent(this, AuthActivity::class.java)
            authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            authActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            startActivity(authActivityIntent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    override fun onStop() {
        super.onStop()

        Log.i("UserProfileActivity: ", "UserProfileActivity onStop()")
        _userProfileActivityBinding = null
    }

    override fun onBackPressed() {
        //super.onBackPressed()

        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(mainActivityIntent)

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
