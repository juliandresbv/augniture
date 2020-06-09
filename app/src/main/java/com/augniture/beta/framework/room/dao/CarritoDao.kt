package com.augniture.beta.framework.room.dao

import android.util.Log
import androidx.room.*

import androidx.room.OnConflictStrategy.*
import com.augniture.beta.framework.room.entity.ProductoCompraEntity

@Dao
interface CarritoDao {

    @Query(value = "SELECT * FROM ProductoCompra")
    suspend fun getProductosCompra(): List<ProductoCompraEntity>

    @Query(value = "SELECT * FROM ProductoCompra WHERE productocompraid = :idProducto LIMIT 1")
    suspend fun getProductoCompra(idProducto: String): List<ProductoCompraEntity>

    @Insert(onConflict = IGNORE)
    suspend fun addProductoCompra(productoCompra: ProductoCompraEntity)

    @Query(value = "UPDATE ProductoCompra SET cantidad = cantidad + 1 WHERE productocompraid = :idProducto")
    suspend fun updateProductoCompraAdd(idProducto: String)

    suspend fun insertOrUpdateProductoCompra(productoCompra: ProductoCompraEntity) {
        val productoEncontrado = getProductoCompra(productoCompra.producto?.id!!)

        if (productoEncontrado.isEmpty()) { addProductoCompra(productoCompra) }
        else { updateProductoCompraAdd(productoEncontrado[0].producto?.id!!) }
    }

    @Query(value = "DELETE FROM ProductoCompra WHERE productocompraid = :idProducto")
    suspend fun deleteProducto(idProducto: String)

    @Query(value = "UPDATE ProductoCompra SET cantidad = cantidad - 1 WHERE productocompraid = :idProducto")
    suspend fun updateProductoCompraSubstract(idProducto: String)

    suspend fun deleteOrUpdateProductoCompra(idProducto: String) {
        val listaProductoCompraEntity = getProductoCompra(idProducto)

        if (listaProductoCompraEntity.isNotEmpty()) {
            val productoCompraEntity = listaProductoCompraEntity[0]

            if (productoCompraEntity.cantidad!! > 1) { updateProductoCompraSubstract(productoCompraEntity.producto?.id!!) }
            else { deleteProducto(productoCompraEntity.producto?.id!!) }
        }
    }

    @Query(value = "DELETE FROM ProductoCompra WHERE 1")
    suspend fun deleteAll()
}