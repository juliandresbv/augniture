package com.augniture.beta.ui.general.productos

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.augniture.beta.dependencyinjection.AugnitureViewModel
import com.augniture.beta.dependencyinjection.Interactors
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra
import com.augniture.beta.domain.Usuario
import com.augniture.beta.ui.home.productcategories.CategoriasProductosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CarritoViewModel(application: Application, interactors: Interactors) : AugnitureViewModel(application, interactors) {

    val productosCompra: MutableLiveData<List<ProductoCompra>> = MutableLiveData()

    fun loadProductosCompra() {
        GlobalScope.launch(Dispatchers.Default) {
            productosCompra.postValue(interactors.getProductosCompra())
        }
    }

    fun addProductoCompra(producto: Producto) {
        GlobalScope.launch(Dispatchers.Default) {
            interactors.addProductoCompra(producto)
            loadProductosCompra()
        }
    }

    fun deleteProductoCompra(producto: Producto) {
        GlobalScope.launch(Dispatchers.Default) {
            interactors.deleteProductoCompra(producto)
            loadProductosCompra()
        }
    }

    fun addProductosOrden(usuario: Usuario) {
        GlobalScope.launch(Dispatchers.Default) {
            interactors.addOrdenCompra(usuario)
            loadProductosCompra()
        }
    }

}