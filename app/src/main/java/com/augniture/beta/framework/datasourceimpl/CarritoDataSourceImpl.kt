package com.augniture.beta.framework.datasourceimpl

import android.content.Context
import android.util.Log
import com.augniture.beta.data.datasource.CarritoDataSource
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.ProductoCompra
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.firebase.dbconfig.AugnitureFirebaseFirestoneDatabaseManager
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.framework.room.dbconfig.AugnitureRoomDatabaseManager
import com.augniture.beta.framework.room.entity.ProductoCompraEntity
import com.augniture.beta.framework.room.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

class CarritoDataSourceImpl(context: Context) : CarritoDataSource {

    // Check if device has Internet connection
    private var isOnline = NetworkManager.isOnline(context)

    /*
    Room
    Endpoint to local persistence with Room.
    */
    private val carritoDao = AugnitureRoomDatabaseManager.getInstance(context).carritoDao()

    /*
    Firebase
    Endpoint to cloud persistence with Firestone.
     */
    private val productoFirestoneCollection = AugnitureFirebaseFirestoneDatabaseManager
        .getFirestoneInstance()
        .collection(AugnitureFirebaseFirestoneDatabaseManager.PRODUCTOS_COLLECTION)

    /*
    Firebase
    Endpoint to cloud persistence with Firestone.
     */
    private val usuarioFirestoneCollection = AugnitureFirebaseFirestoneDatabaseManager
        .getFirestoneInstance()
        .collection(AugnitureFirebaseFirestoneDatabaseManager.USUARIOS_COLLECTION)

    override suspend fun readAll(): List<ProductoCompra> {

        return carritoDao.getProductosCompra().map{
            val productoCompraEntity = it
            val producto = Producto(
                it.producto?.id,
                it.producto?.nombre,
                it.producto?.imagen,
                it.producto?.destacado,
                it.producto?.descripcion,
                it.producto?.categoria,
                it.producto?.precio,
                it.producto?.modeloar
            )

            ProductoCompra(
                productoCompraEntity.cantidad,
                producto
            )
        }

    }

    override suspend fun addLocal(producto: Producto) {

        val productoEntity = ProductoEntity(
            producto.id!!,
            producto.nombre,
            producto.imagen,
            producto.destacado,
            producto.descripcion,
            producto.categoria,
            producto.precio,
            producto.modeloar
        )

        val productoCompraEntity = ProductoCompraEntity(
            UUID.randomUUID().toString(),
            productoEntity,
            1
        )

        carritoDao.insertOrUpdateProductoCompra(productoCompraEntity)

        //readAll()

    }

    override suspend fun removeLocal(producto: Producto) {

        carritoDao.deleteOrUpdateProductoCompra(producto.id!!)

        //readAll()

    }

    override suspend fun addExternal(usuario: Usuario) {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {

            val idUsuario = usuario.id

            val productosCompra = carritoDao.getProductosCompra().map{
                val productoCompraEntity = it
                val producto = Producto(
                    it.producto?.id,
                    it.producto?.nombre,
                    it.producto?.imagen,
                    it.producto?.destacado,
                    it.producto?.descripcion,
                    it.producto?.categoria,
                    it.producto?.precio,
                    it.producto?.modeloar
                )

                ProductoCompra(
                    productoCompraEntity.cantidad,
                    producto
                )
            }

            if (productosCompra.isNotEmpty()) {

                val productosOrden = productosCompra.map{
                    val productoCompra = it

                    val referenciaProducto = productoFirestoneCollection.document(productoCompra.producto?.id!!)

                    hashMapOf<String, Any>(
                        "quantity" to productoCompra.cantidad!!,
                        "product" to referenciaProducto
                    )
                }

                usuarioFirestoneCollection
                    .document(idUsuario!!)
                    .collection(AugnitureFirebaseFirestoneDatabaseManager.ORDENES_COLLECTION)
                    .add(hashMapOf<String, Any>(
                        "products" to productosOrden
                    ))
                    .await()

                Log.i("CarritoDataSourceImpl: ", "CarritoDataSourceImpl IF")
                carritoDao.deleteAll()
            }

            Log.i("CarritoDataSourceImpl: ", "CarritoDataSourceImpl OUT")
            GlobalScope.launch(Dispatchers.Default) { carritoDao.deleteAll() }

        }

    }

}