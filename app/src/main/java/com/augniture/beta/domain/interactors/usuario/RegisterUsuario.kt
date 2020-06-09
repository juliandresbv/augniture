package com.augniture.beta.domain.interactors.usuario

import com.augniture.beta.data.repository.UsuarioRepository
import com.augniture.beta.domain.CredencialesUsuario
import com.augniture.beta.domain.RegistroUsuario
import com.augniture.beta.domain.Usuario

class RegisterUsuario(private val usuarioRepository: UsuarioRepository) {
    suspend operator fun invoke(registroUsuario: RegistroUsuario) = usuarioRepository.registerUsuario(registroUsuario)
}