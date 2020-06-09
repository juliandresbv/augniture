package com.augniture.beta.ui.auth.register


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
import com.augniture.beta.databinding.FragmentRegisterBinding
import com.augniture.beta.dependencyinjection.AugnitureViewModelFactory
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.analytics.AmplitudeAnalyticsCons
import com.augniture.beta.framework.firebase.auth.AugnitureFirebaseAuthManager
import com.augniture.beta.framework.firebase.auth.FirebaseAuthCons
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.ui.AuthActivity
import com.augniture.beta.ui.MainActivity
import com.augniture.beta.ui.auth.AuthViewModel
import com.augniture.beta.ui.auth.signin.SignInFragment
import com.augniture.beta.ui.supportutilities.SharedPreferencesConstants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator
import kotlinx.android.synthetic.main.fragment_register.*
import java.lang.Exception


class RegisterFragment : Fragment() {

    private var _fragmentRegisterBinding: FragmentRegisterBinding? = null
    private val fragmentRegisterBinding get() = _fragmentRegisterBinding!!

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        amplitudeInstance = Amplitude
            .getInstance()
            .initialize(requireActivity(), getString(R.string.amplitude_ak))
            .enableForegroundTracking(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        val registerFragmentView = fragmentRegisterBinding.root

        googleSignInClient = GoogleSignIn.getClient(
            requireActivity(),
            AugnitureFirebaseAuthManager.getGoogleSignInOptions(requireActivity())
        )

        // Setup SignInViewModel
        authViewModel = ViewModelProviders.of(requireActivity(), AugnitureViewModelFactory).get(
            AuthViewModel::class.java)

        // Setup shared preferences for Usuario info
        sharedPreferences = requireActivity().getSharedPreferences(SharedPreferencesConstants.SP_SESION_USUARIO, Context.MODE_PRIVATE)

        return registerFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerEmailTxtInput: TextInputEditText = fragmentRegisterBinding.registerEmailTxtInput
        val registerPasswordTxtInput: TextInputEditText = fragmentRegisterBinding.registerPasswordTxtInput
        val registerNameTxtInput: TextInputEditText = fragmentRegisterBinding.registerNameTxtInput

        registerEmailTxtInput.addTextChangedListener(registerTextWatcher)
        registerPasswordTxtInput.addTextChangedListener(registerTextWatcher)
        registerNameTxtInput.addTextChangedListener(registerTextWatcher)

        registerBtn.setOnClickListener {
            isOnline = NetworkManager.isOnline(requireActivity())

            if (isOnline) {
                val emailString = registerEmailTxtInput.text.toString().trim()
                val passwordString = registerPasswordTxtInput.text.toString().trim()
                val nameString = registerNameTxtInput.text.toString().trim()

                val registroUsuario = RegistroUsuario(emailString, passwordString, nameString)

                authViewModel.registerAuthUsuario(registroUsuario)
            } else {
                val authActivityView = (requireActivity() as AuthActivity).authActivityBinding.root

                val snackbar = Snackbar.make(
                    authActivityView,
                    "No hay conexión a Internet. \n" +
                            "No es posible realizar el registro.",
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

        linkSignIn.setOnClickListener{
            val signInFragment = requireActivity().supportFragmentManager.findFragmentByTag(SignInFragment::class.simpleName) ?: SignInFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.contentAuth, signInFragment, SignInFragment::class.simpleName)
            fragmentTransaction.commit()
        }

        // Google SignIn Button
        google_register_button.setOnClickListener{
            isOnline = NetworkManager.isOnline(requireActivity())

            if (isOnline) {
                googleSignInFlow()
            } else {
                val authActivityView = (requireActivity() as AuthActivity).authActivityBinding.root

                val snackbar = Snackbar.make(
                    authActivityView,
                    "No hay conexión a Internet. \n" +
                            "No es posible realizar el registro.",
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
                Log.i("REG", usuario.id)

                when(usuario.id) {
                    Usuario.USUARIO_EXCEPCION.id -> {

                    }
                }

            } else {
                var sharedPreferenceEditor = sharedPreferences.edit()

                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_ID, usuario.id)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_NOMBRE, usuario.nombre)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_EMAIL, usuario.email)
                sharedPreferenceEditor.putString(SharedPreferencesConstants.KEY_USUARIO_ACTUAL_FOTO, usuario.foto)

                sharedPreferenceEditor.apply()

                // Amplitude Registro: Sign In
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

    override fun onStop() {
        super.onStop()


    }

    private val registerTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val nameIsValid = registerNameTxtInput.text.toString().trim()
                .validator()
                .nonEmpty()
                .addErrorCallback {
                    registerNameTxtInput.error = "Oops!, parece que no has un nombre"
                }
                .addSuccessCallback {
                    registerNameTxtInput.error = null
                }
                .check()

            val emailIsValid = registerEmailTxtInput.text.toString().trim()
                .validator()
                .validEmail()
                .nonEmpty()
                .addErrorCallback {
                    registerEmailTxtInput.error = "Oops!, parece que no has ingresado un email valido"
                }
                .addSuccessCallback {
                    registerEmailTxtInput.error = null
                }
                .check()

            val passwordIsValid = registerPasswordTxtInput.text.toString().trim()
                .validator()
                .nonEmpty()
                .addErrorCallback {
                    registerPasswordTxtInput.error = "Oops!, parece que no has ingresado una contraseña"
                }
                .addSuccessCallback {
                    registerPasswordTxtInput.error = null
                }
                .check()

            registerBtn.isEnabled = emailIsValid && passwordIsValid && nameIsValid
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