package com.augniture.beta.framework.room.dbconfig

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.augniture.beta.framework.room.dao.CarritoDao
import com.augniture.beta.framework.room.dao.FavoritoDao
import com.augniture.beta.framework.room.dao.ProductoDao
import com.augniture.beta.framework.room.dao.UsuarioDao
import com.augniture.beta.framework.room.entity.FavoritoEntity
import com.augniture.beta.framework.room.entity.ProductoCompraEntity
import com.augniture.beta.framework.room.entity.ProductoEntity
import com.augniture.beta.framework.room.entity.UsuarioEntity

@Database(
    entities = arrayOf(
        ProductoEntity::class,
        UsuarioEntity::class,
        FavoritoEntity::class,
        ProductoCompraEntity::class
    ),
    version = 1,
    exportSchema = false
)
abstract class AugnitureRoomDatabaseManager : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "augniture.db"

        private var instance: AugnitureRoomDatabaseManager? = null

        private fun create(context: Context): AugnitureRoomDatabaseManager =
            Room.databaseBuilder(context, AugnitureRoomDatabaseManager::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()

        fun getInstance(context: Context): AugnitureRoomDatabaseManager =
            (instance ?: create(context)).also { instance = it }

    }

    abstract fun productoDao(): ProductoDao

    abstract fun usuarioDao(): UsuarioDao

    abstract fun favoritoDao(): FavoritoDao

    abstract fun carritoDao(): CarritoDao

}