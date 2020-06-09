package com.augniture.beta.data.datasource

import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario

interface UsuarioDataSource {

    suspend fun signIn(credencialesUsuario: CredencialesUsuario, proveedor: String): Usuario

    suspend fun register(registroUsuario: RegistroUsuario): Usuario

    suspend fun signOut()

    suspend fun getCurrent(): Usuario

    suspend fun add(usuario: Usuario)

    //suspend fun readAll(): List<Usuario>

    //suspend fun read(usuario: Usuario): Producto

    //suspend fun remove(usuario: Usuario)

}