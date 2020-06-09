package com.augniture.beta.ui.home.productcategories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.augniture.beta.R
import com.augniture.beta.dependencyinjection.AugnitureViewModel
import com.augniture.beta.dependencyinjection.Interactors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class CategoriasProductosViewModel(application: Application, interactors: Interactors): AugnitureViewModel(application, interactors) {

    data class Categoria(
        val nombre: String?,
        val etiqueta: String?,
        val referenciaIcono: Int?
    )

    companion object {
        val listaCategorias: List<Categoria> = listOf(
            Categoria("Alcoba", "bedroom", R.drawable.ic_category_room),
            Categoria("Baño", "bathroom", R.drawable.ic_category_bathroom),
            Categoria("Decoracion", "decor", R.drawable.ic_category_decoration),
            Categoria("Cocina", "kitchen", R.drawable.ic_category_kitchen),
            Categoria("Comedor", "dinning-room", R.drawable.ic_category_dining),
            Categoria("Sala", "living-room", R.drawable.ic_category_living)
        )
    }

    val categorias: MutableLiveData<List<Categoria>> = MutableLiveData()

    val categoriaSeleccionada: MutableLiveData<Categoria> = MutableLiveData()

    fun loadCategorias() {
        viewModelScope.launch(Dispatchers.Default) {
            categorias.postValue(listaCategorias)
        }
    }

    fun setCategoriaSeleccionada(categoria: Categoria) {
        viewModelScope.launch(Dispatchers.Default) {
            categoriaSeleccionada.postValue(categoria)
        }
    }

}
