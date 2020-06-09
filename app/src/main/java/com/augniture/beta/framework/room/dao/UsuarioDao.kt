package com.augniture.beta.framework.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

import androidx.room.OnConflictStrategy.*

import com.augniture.beta.framework.room.entity.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert(onConflict = IGNORE)
    suspend fun addUsuario(usuario: UsuarioEntity)

    //@Query(value = "SELECT * FROM Producto")
    //suspend fun getProductos(): List<UsuarioEntity>

    //@Query(value = "SELECT * FROM Producto WHERE id = :id")
    //suspend fun getProducto(id: String): ProductoEntity

    //@Delete
    //suspend fun deleteProducto(producto: ProductoEntity)

}