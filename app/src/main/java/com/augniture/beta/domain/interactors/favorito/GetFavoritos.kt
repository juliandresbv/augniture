package com.augniture.beta.domain.interactors.favorito

import com.augniture.beta.data.repository.FavoritoRepository
import com.augniture.beta.data.repository.ProductoRepository
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Usuario

class GetFavoritos(private val favoritoRepository: FavoritoRepository) {
    suspend operator fun invoke(usuario: Usuario) = favoritoRepository.getFavoritos(usuario)
}