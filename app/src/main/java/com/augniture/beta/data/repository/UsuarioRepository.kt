package com.augniture.beta.data.repository

import com.augniture.beta.data.datasource.UsuarioDataSource
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario

class UsuarioRepository(private val usuarioDataSource: UsuarioDataSource) {

    suspend fun signInUsuario(credencialesUsuario: CredencialesUsuario, proveedor: String) = usuarioDataSource.signIn(credencialesUsuario, proveedor)

    suspend fun registerUsuario(registroUsuario: RegistroUsuario) = usuarioDataSource.register(registroUsuario)

    suspend fun signOutUsuario() = usuarioDataSource.signOut()

    suspend fun getCurrentUsuario() = usuarioDataSource.getCurrent()

    suspend fun addUsuario(usuario: Usuario) = usuarioDataSource.add(usuario)

}