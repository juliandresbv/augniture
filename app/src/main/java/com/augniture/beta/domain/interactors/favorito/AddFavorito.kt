package com.augniture.beta.domain.interactors.producto

import com.augniture.beta.data.repository.FavoritoRepository
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario

class AddFavorito(private val favoritoRepository: FavoritoRepository) {
    suspend operator fun invoke(usuario: Usuario, producto: Producto) = favoritoRepository.addFavorito(usuario, producto)
}