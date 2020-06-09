package com.augniture.beta.framework.firebase.auth

import android.content.Context
import android.content.res.Resources
import com.augniture.beta.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

abstract class AugnitureFirebaseAuthManager {

    companion object {

        private var firebaseAuthInstance: FirebaseAuth = FirebaseAuth.getInstance()

        fun getFirebaseAuthInstance(): FirebaseAuth = firebaseAuthInstance

        fun getGoogleSignInOptions(context: Context): GoogleSignInOptions {
            return GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        }

    }

}