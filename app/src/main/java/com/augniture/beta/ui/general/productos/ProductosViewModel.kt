package com.augniture.beta.ui.general.productos

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amplitude.api.Amplitude
import com.amplitude.api.AmplitudeClient
import com.augniture.beta.R
import com.augniture.beta.dependencyinjection.AugnitureViewModel
import com.augniture.beta.dependencyinjection.Interactors
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario
import com.augniture.beta.ui.home.productcategories.CategoriasProductosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProductosViewModel(application: Application, interactors: Interactors) : AugnitureViewModel(application, interactors) {

    val productos: MutableLiveData<List<Producto>> = MutableLiveData()

    val productosCategoria: MutableLiveData<List<Producto>> = MutableLiveData()

    val productosBusqueda: MutableLiveData<List<Producto>> = MutableLiveData()

    val productosFavoritos: MutableLiveData<List<Favorito>> = MutableLiveData()

    val productoSeleccionado: MutableLiveData<Producto> = MutableLiveData()

    private lateinit var amplitudeInstance: AmplitudeClient

    fun loadProductos() {
        viewModelScope.launch(Dispatchers.Default) {
            productos.postValue(interactors.getProductos())
        }
    }

    fun loadProductosCategoria(categoria: CategoriasProductosViewModel.Categoria) {
        viewModelScope.launch(Dispatchers.Default) {
            val productosFiltradosCategoria = interactors.getProductos().filter {
                val producto = it
                producto.categoria == categoria.etiqueta
            }

            productosCategoria.postValue(productosFiltradosCategoria)
        }
    }

    fun loadProductosBusqueda(busqueda: String) {
        viewModelScope.launch(Dispatchers.Default) {
            var productosFiltradosBusqueda: List<Producto> = interactors.getProductos()

            if (busqueda.length >= 3 && !busqueda.isNullOrBlank() && !busqueda.isNullOrEmpty()) {
                productosFiltradosBusqueda = productosFiltradosBusqueda.filter {

                    amplitudeInstance = Amplitude
                        .getInstance()
                        .initialize(application, application.getString(R.string.amplitude_ak))
                        .enableForegroundTracking(application)

                    val data = JSONObject(hashMapOf(
                        "searchTerm" to busqueda,
                        "origin" to "Busqueda"
                    ))

                    amplitudeInstance.logEvent("search", data)

                    val producto = it

                    producto.nombre?.contains(busqueda, true)!!
                }
            }

            productosBusqueda.postValue(productosFiltradosBusqueda)
        }
    }

    fun loadProductosFavoritos(usuario: Usuario) {
        viewModelScope.launch(Dispatchers.Default) {
            productosFavoritos.postValue(interactors.getFavoritos(usuario))
        }
    }

    fun setProductoSeleccionado(producto: Producto) {
        viewModelScope.launch(Dispatchers.Default) {
            productoSeleccionado.postValue(producto)
        }
    }

}