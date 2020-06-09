package com.augniture.beta.domain

data class Usuario(
    var id: String?,
    var nombre: String?,
    var email: String?,
    var foto: String?
) {
    companion object {
        // Moldes de usuarios para manejo de casos
        var USUARIO_VACIO = Usuario("", "", "", "")
        var USUARIO_NULO = Usuario("USUARIO_NULO_ID", "", "", "")
        var USUARIO_EXCEPCION = Usuario("USUARIO_EXCEPCION_ID","","","")
        var USUARIO_NO_CONEXION = Usuario("USUARIO_NO_CONEXION_ID","","","")
    }
}