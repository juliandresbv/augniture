package com.augniture.beta.domain.interactors.productocompra

import com.augniture.beta.data.repository.CarritoRepository

class GetProductosCompra(private val carritoRepository: CarritoRepository) {
    suspend operator fun invoke() = carritoRepository.getProductosCompra()
}