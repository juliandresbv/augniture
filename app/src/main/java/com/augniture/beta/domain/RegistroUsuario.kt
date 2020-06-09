package com.augniture.beta.domain

data class RegistroUsuario(
    var email: String?,
    var contrasena: String?,
    var nombre: String?
) {
    companion object {
        var REGISTRO_VACIO = RegistroUsuario("","", "")
    }
}