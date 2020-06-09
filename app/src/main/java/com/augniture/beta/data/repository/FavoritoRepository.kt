package com.augniture.beta.data.repository

import com.augniture.beta.data.datasource.FavoritoDataSource
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario

class FavoritoRepository(private val favoritoDataSource: FavoritoDataSource) {

    suspend fun getFavoritos(usuario: Usuario) = favoritoDataSource.readAll(usuario)

    suspend fun addFavorito(usuario: Usuario, productoFavorito: Producto) = favoritoDataSource.add(usuario, productoFavorito)

    suspend fun deleteFavorito(usuario: Usuario, productoNoFavorito: Favorito) = favoritoDataSource.remove(usuario, productoNoFavorito)

    //suspend fun getFavorito(favorito: Favorito) = productoDataSource.read(favorito)

}