package com.augniture.beta.data.datasource

import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra
import com.augniture.beta.domain.Usuario

interface CarritoDataSource {

    suspend fun readAll(): List<ProductoCompra>

    suspend fun addLocal(producto: Producto)

    suspend fun removeLocal(producto: Producto)

    suspend fun addExternal(usuario: Usuario)

}