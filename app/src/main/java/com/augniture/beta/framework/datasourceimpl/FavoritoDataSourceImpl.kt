package com.augniture.beta.framework.datasourceimpl

import android.content.Context
import android.util.Log
import com.augniture.beta.data.datasource.FavoritoDataSource
import com.augniture.beta.domain.Favorito
import com.augniture.beta.domain.Producto
import com.augniture.beta.domain.Usuario
import com.augniture.beta.framework.firebase.dbconfig.AugnitureFirebaseFirestoneDatabaseManager
import com.augniture.beta.framework.network.NetworkManager
import com.augniture.beta.framework.room.dbconfig.AugnitureRoomDatabaseManager
import com.augniture.beta.framework.room.entity.FavoritoEntity
import com.augniture.beta.framework.room.entity.ProductoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.*

class FavoritoDataSourceImpl(context: Context) : FavoritoDataSource {

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
    private val favoritoDao = AugnitureRoomDatabaseManager.getInstance(context).favoritoDao()

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


    override suspend fun readAll(usuario: Usuario): List<Favorito> {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            return try {
                val favoritos =
                    usuarioFirestoneCollection
                        .document(usuario.id!!)
                        .collection(AugnitureFirebaseFirestoneDatabaseManager.FAVORITOS_COLLECTION)
                        .get()
                        .await()

                val listaProductosFavoritos = favoritos.documents
                    .map {
                        val producto = it.getDocumentReference("product")
                            ?.get()
                            ?.await()

                        Favorito(
                            it.id,
                            Producto(
                                producto?.id,
                                producto?.getString("title"),
                                producto?.getString("image"),
                                producto?.getBoolean("featured"),
                                producto?.getString("description"),
                                producto?.getString("category"),
                                producto?.getDouble("price"),
                                producto?.getString("arPath")
                            )
                        )
                    }

                for (favorito in listaProductosFavoritos) {
                    var producto = favorito.referenciaProducto

                    var productEntity = ProductoEntity(
                        producto?.id!!,
                        producto.nombre,
                        producto.imagen,
                        producto.destacado,
                        producto.descripcion,
                        producto.categoria,
                        producto.precio,
                        producto.modeloar
                    )

                    var favoritoEntity = FavoritoEntity(favorito.id!!, productEntity, usuario.id)

                    GlobalScope.launch(Dispatchers.Default) {
                        favoritoDao.addFavorito(favoritoEntity)
                    }
                }

                listaProductosFavoritos
            } catch (e: Exception) {
                return emptyList<Favorito>()
            }
        } else {
            return favoritoDao.getFavoritos(usuario.id!!).map {
                var productoEntity = it.producto

                var producto = Producto(
                    productoEntity?.id,
                    productoEntity?.nombre,
                    productoEntity?.imagen,
                    productoEntity?.destacado,
                    productoEntity?.descripcion,
                    productoEntity?.categoria,
                    productoEntity?.precio,
                    productoEntity?.modeloar
                )

                Favorito(
                    it.id,
                    producto
                )
            }

        }

    }

    override suspend fun add(usuario: Usuario, productoFavorito: Producto) {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            val idProducto = productoFavorito.id
            val idUsuario = usuario.id

            val producto =
                productoFirestoneCollection
                    .document(idProducto!!)

            usuarioFirestoneCollection
                .document(idUsuario!!)
                .collection(AugnitureFirebaseFirestoneDatabaseManager.FAVORITOS_COLLECTION)
                .add(hashMapOf(
                    "product" to producto
                ))
                .await()
        }
        else {
            //TODO("Not yet implemented")
            return
        }

    }

    override suspend fun remove(usuario: Usuario, productoNoFavorito: Favorito) {
        isOnline = NetworkManager.isOnlineSimple()

        if (isOnline) {
            val idProducto = productoNoFavorito.id
            val idUsuario = usuario.id

            usuarioFirestoneCollection
                .document(idUsuario!!)
                .collection(AugnitureFirebaseFirestoneDatabaseManager.FAVORITOS_COLLECTION)
                .document(idProducto!!)
                .delete()
                .await()

            /*
            val producto = productoNoFavorito.referenciaProducto
            val productoEntity = ProductoEntity(
                producto?.id!!,
                producto.nombre,
                producto.imagen,
                producto.destacado,
                producto.descripcion,
                producto.categoria,
                producto.precio,
                producto.modeloar
            )
            val favoritoEntity = FavoritoEntity(productoNoFavorito.id, productoEntity, usuario.id)
            */

            GlobalScope.launch(Dispatchers.Default) { favoritoDao.deleteFavorito(usuario.id!!, productoNoFavorito.id) }

        }

    }

}