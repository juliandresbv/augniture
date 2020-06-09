package com.augniture.beta.domain

import com.google.firebase.firestore.DocumentReference

data class Favorito(
    val id: String?,
    val referenciaProducto: Producto?
) {
}