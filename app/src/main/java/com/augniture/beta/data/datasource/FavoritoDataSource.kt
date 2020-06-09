package com.augniture.beta.data.datasource

import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario

interface FavoritoDataSource {

    suspend fun readAll(usuario: Usuario): List<Favorito>

    suspend fun add(usuario: Usuario, productoFavorito: Producto)

    suspend fun remove(usuario: Usuario, productoNoFavorito: Favorito)

    //suspend fun read(favorito: Favorito): Producto

}