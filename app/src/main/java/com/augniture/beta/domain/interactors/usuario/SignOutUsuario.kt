package com.augniture.beta.domain.interactors.usuario

import com.augniture.beta.data.repository.UsuarioRepository
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.Usuario

class SignOutUsuario(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke() = usuarioRepository.signOutUsuario()
}