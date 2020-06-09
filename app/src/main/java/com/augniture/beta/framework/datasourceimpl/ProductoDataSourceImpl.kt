package com.augniture.beta.framework.datasourceimpl

import android.content.Context
import android.util.Log
import com.augniture.beta.data.datasource.ProductoDataSource
import com.augniture.beta.domain.Producto
import com.augniture.beta.framework.firebase.dbconfig.AugnitureFirebaseFirestoneDatabaseManager
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.framework.room.dbconfig.AugnitureRoomDatabaseManager
import com.augniture.beta.framework.room.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.*
import java.lang.ref.WeakReference

class ProductoDataSourceImpl(context: Context) : ProductoDataSource {

    companion object {
        // This simulates current connection to Internet so the Data Source connects to Firestore
        const val FIREBASE = true
    }

    // Check if device has Internet connection
    private var isOnline = NetworkManager.isOnline(context)

    /*
    Room
    Endpoint to local persistence with Room.
    */
    private val productoDao = AugnitureRoomDatabaseManager.getInstance(context).productoDao()

    /*
    Firebase
    Endpoint to cloud persistence with Firestone.
     */
    private val productoFirestoneCollection = AugnitureFirebaseFirestoneDatabaseManager
        .getFirestoneInstance()
        .collection(AugnitureFirebaseFirestoneDatabaseManager.PRODUCTOS_COLLECTION)

    override suspend fun add(producto: Producto) {
        return productoDao.addProducto(
            ProductoEntity(
                producto.id!!,
                producto.nombre,
                producto.imagen,
                producto.destacado,
                producto.descripcion,
                producto.categoria,
                producto.precio,
                producto.modeloar
            )
        )
    }

    override suspend fun readAll(): List<Producto> {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            try {
                val productos =
                    WeakReference(
                        productoFirestoneCollection
                        .get()
                        .await()
                    )

                val lista = WeakReference(
                    productos.get()?.documents!!.map {
                        Producto(
                            it.id,
                            it.getString("title"),
                            it.getString("image"),
                            it.getBoolean("featured"),
                            it.getString("description"),
                            it.getString("category"),
                            it.getDouble("price"),
                            it.getString("arPath")
                        )
                    }
                )
                /*
                val listaProductos: List<Producto> = productos.get()?.documents!!.map {
                    Producto(
                        it.id,
                        it.getString("title"),
                        it.getString("image"),
                        it.getBoolean("featured"),
                        it.getString("description"),
                        it.getString("category"),
                        it.getDouble("price"),
                        it.getString("arPath")
                    )
                }

                 */

                // Here the retrieved data from Firestone should be persisted in Room
                if (lista.get() != null) {
                    for (producto in lista.get()!!) {
                        GlobalScope.launch(Dispatchers.Default) {
                            add(producto)
                        }
                    }
                    return lista.get()!!
                }

                return emptyList<Producto>()
            } catch (e: Exception) {
                Log.d("ProductoDataSourceImpl: ", "Online Exception: $e")
                return emptyList<Producto>()
            }
        } else {
            try {
                return (productoDao.getProductos().map {
                    Producto(
                        it.id,
                        it.nombre,
                        it.imagen,
                        it.destacado,
                        it.descripcion,
                        it.categoria,
                        it.precio,
                        it.modeloar
                    )
                })
            } catch (e: Exception) {
                Log.d("ProductoDataSourceImpl: ", "Offline Exception: $e")
                return emptyList<Producto>()
            }
        }

    }

}