package com.augniture.beta.domain

data class CredencialesUsuario(
    var email: String?,
    var contrasena: String?
) {
    companion object {
        val CREDENCIALES_USUARIO_VACIAS = CredencialesUsuario("","")
    }
}