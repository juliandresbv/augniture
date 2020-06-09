package com.augniture.beta.framework.firebase.dbconfig

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class AugnitureFirebaseFirestoneDatabaseManager {

    companion object {

        const val PRODUCTOS_COLLECTION: String = "products"

        const val USUARIOS_COLLECTION: String = "users"

        const val FAVORITOS_COLLECTION: String = "favorites"

        const val ORDENES_COLLECTION: String = "orders"

        val settings = FirebaseFirestoreSettings
            .Builder()
            .setPersistenceEnabled(true)
            .build()

        private var firestone_instance: FirebaseFirestore = FirebaseFirestore.getInstance()

        fun getFirestoneInstance(): FirebaseFirestore = firestone_instance
    }

}