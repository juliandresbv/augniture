package com.augniture.beta.domain.interactors.productocompra

import com.augniture.beta.data.repository.CarritoRepository
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra

class AddProductoCompra(private val carritoRepository: CarritoRepository) {
    suspend operator fun invoke(producto: Producto) = carritoRepository.addProductoCompra(producto)
}