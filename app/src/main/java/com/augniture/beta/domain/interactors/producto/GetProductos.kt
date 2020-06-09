package com.augniture.beta.domain.interactors.producto

import com.augniture.beta.data.repository.ProductoRepository

class GetProductos(private val productoRepository: ProductoRepository) {
    suspend operator fun invoke() = productoRepository.getProductos()
}