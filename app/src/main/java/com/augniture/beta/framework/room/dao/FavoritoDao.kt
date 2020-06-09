package com.augniture.beta.framework.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

import androidx.room.OnConflictStrategy.*
import com.augniture.beta.framework.room.entity.FavoritoEntity

import com.augniture.beta.framework.room.entity.ProductoEntity
import com.augniture.beta.framework.room.entity.UsuarioEntity

@Dao
interface FavoritoDao {

    @Insert(onConflict = IGNORE)
    suspend fun addFavorito(productoFavorito: FavoritoEntity)

    @Query(value = "SELECT * FROM Favorito WHERE Favorito.idusuario = :idUsuario")
    suspend fun getFavoritos(idUsuario: String): List<FavoritoEntity>

    //@Delete
    @Query(value = "DELETE FROM Favorito WHERE (Favorito.idusuario = :idUsuario AND Favorito.id = :idFavorito)")
    suspend fun deleteFavorito(idUsuario: String, idFavorito: String)

    //@Query(value = "SELECT * FROM Producto WHERE id = :id")
    //suspend fun getProducto(id: String): ProductoEntity
}