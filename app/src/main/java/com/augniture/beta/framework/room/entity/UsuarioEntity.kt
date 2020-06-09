package com.augniture.beta.framework.room.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuario")
data class UsuarioEntity(
    @NonNull @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "nombre") val name: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "foto") val photo: String?
)