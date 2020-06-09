package com.augniture.beta.ui.auth

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.augniture.beta.dependencyinjection.AugnitureViewModel
import com.augniture.beta.dependencyinjection.Interactors
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application, interactors: Interactors) : AugnitureViewModel(application, interactors) {

    val signedInUsuario: MutableLiveData<Usuario> = MutableLiveData()

    fun loadAuthUsuario(credencialesUsuario: CredencialesUsuario, proveedor: String) {
        GlobalScope.launch(Dispatchers.Default) {
            signedInUsuario.postValue(interactors.signInUsuario(credencialesUsuario, proveedor))
        }
    }

    fun registerAuthUsuario(registroUsuario: RegistroUsuario) {
        GlobalScope.launch(Dispatchers.Default) {
            signedInUsuario.postValue(interactors.registerUsuario(registroUsuario))
        }
    }

    fun signOutUsuario() {
        GlobalScope.launch(Dispatchers.Default) {
            interactors.signOutUsuario()
            signedInUsuario.postValue(null)
        }
    }

}