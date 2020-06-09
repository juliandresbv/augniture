package com.augniture.beta.framework.room.entity

import androidx.annotation.NonNull
import androidx.room.*

@Entity(tableName = "Favorito", indices = arrayOf(Index(value = ["productoid"], unique = true)))
data class FavoritoEntity(
    @NonNull @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @Embedded(prefix = "producto") val producto: ProductoEntity?,
    @ColumnInfo(name = "idusuario") val idUsuario: String?
)