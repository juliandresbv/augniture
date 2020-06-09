package com.augniture.beta.data.repository

import com.augniture.beta.data.datasource.CarritoDataSource
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario

class CarritoRepository(private val carritoDataSource: CarritoDataSource) {

    suspend fun getProductosCompra() = carritoDataSource.readAll()

    suspend fun addProductoCompra(producto: Producto) = carritoDataSource.addLocal(producto)

    suspend fun deleteProductoCompra(producto: Producto) = carritoDataSource.removeLocal(producto)

    suspend fun addOrdenCompra(usuario: Usuario) = carritoDataSource.addExternal(usuario)

}