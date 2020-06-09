package com.augniture.beta.domain.interactors.productocompra

import com.augniture.beta.data.repository.CarritoRepository
import com.augniture.beta.domain.Usuario

class AddOrdenCompra(private val carritoRepository: CarritoRepository) {
    suspend operator fun invoke(usuario: Usuario) = carritoRepository.addOrdenCompra(usuario)
}