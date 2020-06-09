package com.augniture.beta.ui.favorites

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.augniture.beta.dependencyinjection.AugnitureViewModel
import com.augniture.beta.dependencyinjection.Interactors
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavoritosViewModel(application: Application, interactors: Interactors) : AugnitureViewModel(application, interactors) {

    fun addFavorito(usuario: Usuario, producto: Producto) {
        viewModelScope.launch(Dispatchers.Default) {
            interactors.addFavorito(usuario, producto)
        }
    }

    fun deleteFavorito(usuario: Usuario, favorito: Favorito) {
        viewModelScope.launch(Dispatchers.Default) {
            interactors.deleteFavorito(usuario, favorito)
        }
    }

}