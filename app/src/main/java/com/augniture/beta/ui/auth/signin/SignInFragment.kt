package com.augniture.beta.ui.auth.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.R
import com.augniture.beta.databinding.FragmentSigninBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.analytics.AmplitudeAnalyticsCons
import com.augniture.beta.framework.firebase.auth.AugnitureFirebaseAuthManager
import com.augniture.beta.framework.firebase.auth.FirebaseAuthCons
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.AuthActivity
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.auth.AuthViewModel
import com.augniture.beta.ui.auth.register.RegisterFragment
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_signin.*
import java.lang.Exception


class SignInFragment : Fragment() {

    private var _fragmentSigninBinding: FragmentSigninBinding? = null
    private val fragmentSigninBinding get() = _fragmentSigninBinding!!

    private lateinit var authViewModel: AuthViewModel

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var amplitudeInstance: AmplitudeClient

    private var isOnline: Boolean = false

    private val GSI_INTENT_ID = 777

    /*
    Firebase Auth
    GoogleSignInClient
     */
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _fragmentSigninBinding = FragmentSigninBinding.inflate(inflater, container, false)
        val signInFragmentView = fragmentSigninBinding.root

        amplitudeInstance = Amplitude
            .getInstance()
            .initialize(requireActivity(), getString(R.string.amplitude_ak))
            .enableForegroundTracking(requireActivity().application)

        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            AugnitureFirebaseAuthManager.getGoogleSignInOptions(requireActivity())
        )

        // Setup SignInViewModel
        authViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(
            AuthViewModel::class.java)

        // Setup shared preferences for Usuario info
        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        return signInFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInEmailTxtInput = fragmentSigninBinding.signInEmailTxtInput
        val signInPasswordTxtInput: TextInputEditText = fragmentSigninBinding.signInPasswordTxtInput

        signInEmailTxtInput.addTextChangedListener(signInTextWatcher)
        signInPasswordTxtInput.addTextChangedListener(signInTextWatcher)

        signInBtn.setOnClickListener {
            isOnline = NetworkManager.isOnline(requireActivity())

            if (isOnline) {
                val emailString = signInEmailTxtInput.text.toString().trim()
                val passwordString = signInPasswordTxtInput.text.toString().trim()

                val credencialesUsuario = CredencialesUsuario(emailString, passwordString)

                authViewModel.loadAuthUsuario(credencialesUsuario, FirebaseAuthCons.EMAIL_PROVIDER)
            } else {
                val authActivityView = (requireActivity() as AuthActivity).authActivityBinding.root

                val snackbar = Snackbar.make(
                    authActivityView,
                    "No hay conexión a Internet. \n" +
                            "No es posible iniciar sesion.",
                    2500)
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.snackbarError, null))
                snackbar.setTextColor(Color.WHITE)
                snackbar.setAction("Cerrar") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }
        }

        linkRegister.setOnClickListener{
            val registerFragment = requireActivity().supportFragmentManager.findFragmentByTag(RegisterFragment::class.simpleName) ?: RegisterFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentAuth, registerFragment, RegisterFragment::class.simpleName)
            fragmentTransaction.commit()
        }

        // Google SignIn Button
        google_sign_in_button.setOnClickListener{
            isOnline = NetworkManager.isOnline(requireActivity())

            if (isOnline) {
                googleSignInFlow()
            } else {
                val authActivityView = (requireActivity() as AuthActivity).authActivityBinding.root

                val snackbar = Snackbar.make(
                    authActivityView,
                    "No hay conexión a Internet. \n" +
                            "No es posible iniciar sesion.",
                    2500)
                snackbar.setActionTextColor(Color.WHITE)
                snackbar.view.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.snackbarError, null))
                snackbar.setTextColor(Color.WHITE)
                snackbar.setAction("Cerrar") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        authViewModel.signedInUsuario.observe(viewLifecycleOwner, Observer {
            val usuario = it

            if (usuario.id != Usuario.USUARIO_EXCEPCION.id &&
                usuario.id != Usuario.USUARIO_NULO.id &&
                usuario.id != Usuario.USUARIO_NO_CONEXION.id) {
                // User sign in failure
                Log.i("SIGN", usuario.id)

            } else {

                var sharedPreferenceEditor = sharedPreferences.edit()

                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID, usuario.id)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_NOMBRE, usuario.nombre)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_EMAIL, usuario.email)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_FOTO, usuario.foto)

                sharedPreferenceEditor.apply()

                amplitudeInstance.logEvent(AmplitudeAnalyticsCons.LOGIN)

                val mainActivityIntent = Intent(requireActivity(), MainActivity::class.java)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

                startActivity(mainActivityIntent)
            }
        })
    }

    private val signInTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val emailIsValid = signInEmailTxtInput.text.toString().trim()
                .validator()
                .validEmail()
                .nonEmpty()
                .addErrorCallback {
                    signInEmailTxtInput.error = "Oops!, parece que no has ingresado un email valido"
                }
                .addSuccessCallback {
                    signInEmailTxtInput.error = null
                }
                .check()

            val passwordIsValid = signInPasswordTxtInput.text.toString().trim()
                .validator()
                .nonEmpty()
                .addErrorCallback {
                    signInPasswordTxtInput.error = "Oops!, parece que no has ingresado una contraseña"
                }
                .addSuccessCallback {
                    signInPasswordTxtInput.error = null
                }
                .check()

            signInBtn.isEnabled = emailIsValid && passwordIsValid
        }

        override fun afterTextChanged(s: Editable) {}
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        var credencialesUsuario = CredencialesUsuario.CREDENCIALES_USUARIO_VACIAS

        try {
            val account = task.getResult(ApiException::class.java)

            if (account != null) {
                credencialesUsuario.email = account.idToken
                credencialesUsuario.contrasena = account.idToken
            }
        }
        catch(e: Exception) {
            // Could not Sign In with Google
        }
        authViewModel.loadAuthUsuario(credencialesUsuario, FirebaseAuthCons.GOOGLE_PROVIDER)
    }

    private fun googleSignInFlow() {
        val googleSignInIntent = googleSignInClient?.signInIntent
        startActivityForResult(googleSignInIntent, GSI_INTENT_ID)
    }

}