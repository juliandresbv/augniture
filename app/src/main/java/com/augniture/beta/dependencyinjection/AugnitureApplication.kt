package com.augniture.beta.dependencyinjection

import android.app.Application
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.augniture.beta.data.repository.CarritoRepository
import com.augniture.beta.data.repository.FavoritoRepository
import com.augniture.beta.data.repository.ProductoRepository
import com.augniture.beta.data.repository.UsuarioRepository
import com.augniture.beta.domain.interactors.favorito.GetFavoritos
import com.augniture.beta.domain.interactors.producto.*
import com.augniture.beta.domain.interactors.productocompra.AddOrdenCompra
import com.augniture.beta.domain.interactors.productocompra.AddProductoCompra
import com.augniture.beta.domain.interactors.productocompra.DeleteProductoCompra
import com.augniture.beta.domain.interactors.productocompra.GetProductosCompra
import com.augniture.beta.domain.interactors.usuario.*
import com.augniture.beta.framework.datasourceimpl.CarritoDataSourceImpl
import com.augniture.beta.framework.datasourceimpl.FavoritoDataSourceImpl
import com.augniture.beta.framework.datasourceimpl.ProductoDataSourceImpl
import com.augniture.beta.framework.datasourceimpl.UsuarioDataSourceImpl
import com.augniture.beta.framework.network.NetworkManager

//import leakcanary.*
//import leakcanary.AppWatcher.objectWatcher

class AugnitureApplication : Application() {
    /*
    companion object {
        fun watch(any: Any, desc: String) {
            AppWatcher.objectWatcher.watch(any, desc)
        }
    }
    */

    override fun onCreate() {
        super.onCreate()

        val productoRepository = ProductoRepository(ProductoDataSourceImpl(this))

        val usuarioRepository = UsuarioRepository(UsuarioDataSourceImpl(this))

        val favoritoRepository = FavoritoRepository(FavoritoDataSourceImpl(this))

        val carritoRepository = CarritoRepository(CarritoDataSourceImpl(this))

        AugnitureViewModelFactory.inject(
            this,
            Interactors(
                SignInUsuario(usuarioRepository),
                RegisterUsuario(usuarioRepository),
                SignOutUsuario(usuarioRepository),
                GetCurrentUsuario(usuarioRepository),
                AddUsuario(usuarioRepository),

                GetProductos(productoRepository),
                AddProducto(productoRepository),

                GetFavoritos(favoritoRepository),
                AddFavorito(favoritoRepository),
                DeleteFavorito(favoritoRepository),

                GetProductosCompra(carritoRepository),
                AddProductoCompra(carritoRepository),
                DeleteProductoCompra(carritoRepository),
                AddOrdenCompra(carritoRepository)
            )
        )
    }

}