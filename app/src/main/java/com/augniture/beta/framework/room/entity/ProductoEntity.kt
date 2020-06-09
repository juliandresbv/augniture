package com.augniture.beta.framework.room.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Producto")
data class ProductoEntity(
    @NonNull @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "nombre") val nombre: String?,
    @ColumnInfo(name = "imagen") val imagen: String?,
    @ColumnInfo(name = "destacado") val destacado: Boolean?,
    @ColumnInfo(name = "descripcion") val descripcion: String?,
    @ColumnInfo(name = "categoria") val categoria: String?,
    @ColumnInfo(name = "precio") val precio: Double?,
    @ColumnInfo(name = "modeloar") val modeloar: String?
)