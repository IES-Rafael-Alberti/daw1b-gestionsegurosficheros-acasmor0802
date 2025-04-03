package org.example.utils

import at.favre.lib.crypto.bcrypt.BCrypt

class Seguridad : IUtilSeguridad {

    override fun encriptarClave(clave: String, nivelSeguridad: Int): String {
        return BCrypt.withDefaults().hashToString(nivelSeguridad, clave.toCharArray())
    }

    override fun verificarClave(claveIngresada: String, hashAlmacenado: String): Boolean {
        return BCrypt.verifyer().verify(claveIngresada.toCharArray(), hashAlmacenado).verified
    }
}