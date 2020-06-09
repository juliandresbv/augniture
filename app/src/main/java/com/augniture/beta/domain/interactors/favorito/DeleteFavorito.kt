package com.augniture.beta.domain.interactors.producto

import com.augniture.beta.data.repository.FavoritoRepository
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario

class DeleteFavorito(private val favoritoRepository: FavoritoRepository) {
    suspend operator fun invoke(usuario: Usuario, productoNoFavorito: Favorito) = favoritoRepository.deleteFavorito(usuario, productoNoFavorito)
}