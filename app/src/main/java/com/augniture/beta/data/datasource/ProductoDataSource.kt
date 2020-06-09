package com.augniture.beta.data.datasource

import com.augniture.beta.domain.Producto

interface ProductoDataSource {

    suspend fun add(producto: Producto)

    suspend fun readAll(): List<Producto>

    //suspend fun read(producto: Producto): Producto

    //suspend fun remove(producto: Producto)

}