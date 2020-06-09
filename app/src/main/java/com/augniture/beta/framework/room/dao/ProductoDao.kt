package com.augniture.beta.framework.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

import androidx.room.OnConflictStrategy.*

import com.augniture.beta.framework.room.entity.ProductoEntity

@Dao
interface ProductoDao {

    @Insert(onConflict = IGNORE)
    suspend fun addProducto(producto: ProductoEntity)

    @Query(value = "SELECT * FROM Producto")
    suspend fun getProductos(): List<ProductoEntity>

    //@Query(value = "SELECT * FROM Producto WHERE id = :id")
    //suspend fun getProducto(id: String): ProductoEntity

    //@Delete
    //suspend fun deleteProducto(producto: ProductoEntity)

}