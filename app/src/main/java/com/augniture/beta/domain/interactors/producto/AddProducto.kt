package com.augniture.beta.domain.interactors.producto

import com.augniture.beta.data.repository.ProductoRepository
import com.augniture.beta.domain.Producto

class AddProducto(private val productoRepository: ProductoRepository) {
    suspend operator fun invoke(producto: Producto) = productoRepository.addProducto(producto)
}