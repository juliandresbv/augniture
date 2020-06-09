package com.augniture.beta.data.repository

import com.augniture.beta.data.datasource.ProductoDataSource
import com.augniture.beta.domain.Producto

class ProductoRepository(private val productoDataSource: ProductoDataSource) {

    suspend fun addProducto(producto: Producto) = productoDataSource.add(producto)

    suspend fun getProductos() = productoDataSource.readAll()

    //suspend fun getProducto(producto: Producto) = productoDataSource.read(producto)

    //suspend fun deleteProducto(producto: Producto) = productoDataSource.remove(producto)
}