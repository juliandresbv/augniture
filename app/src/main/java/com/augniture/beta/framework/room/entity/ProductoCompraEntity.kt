package com.augniture.beta.framework.room.entity

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "ProductoCompra", indices = arrayOf(Index(value = ["productocompraid"], unique = true)))
data class ProductoCompraEntity(
    @NonNull @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @Embedded(prefix = "productocompra") val producto: ProductoEntity?,
    @ColumnInfo(name = "cantidad") val cantidad: Int?
)