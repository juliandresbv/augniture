package com.augniture.beta.dependencyinjection

import com.augniture.beta.domain.interactors.favorito.GetFavoritos
import com.augniture.beta.domain.interactors.producto.*
import com.augniture.beta.domain.interactors.productocompra.AddProductoCompra
import com.augniture.beta.domain.interactors.productocompra.AddOrdenCompra
import com.augniture.beta.domain.interactors.productocompra.DeleteProductoCompra
import com.augniture.beta.domain.interactors.productocompra.GetProductosCompra
import com.augniture.beta.domain.interactors.usuario.*

data class Interactors(
    val signInUsuario: SignInUsuario,
    val registerUsuario: RegisterUsuario,
    val signOutUsuario: SignOutUsuario,
    val getCurrentUsuario: GetCurrentUsuario,
    val addUsuario: AddUsuario,
    val getProductos : GetProductos,
    val addProducto: AddProducto,
    val getFavoritos: GetFavoritos,
    val addFavorito: AddFavorito,
    val deleteFavorito: DeleteFavorito,
    val getProductosCompra: GetProductosCompra,
    val addProductoCompra: AddProductoCompra,
    val deleteProductoCompra: DeleteProductoCompra,
    val addOrdenCompra: AddOrdenCompra
)