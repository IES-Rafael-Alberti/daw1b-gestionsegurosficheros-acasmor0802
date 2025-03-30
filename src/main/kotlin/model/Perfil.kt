package org.example.model

enum class Perfil {
    ADMIN, GESTION, CONSULTA;

    companion object {
        fun getPerfil(valor: String): Perfil {
            return try {
                valueOf(valor.uppercase())
            } catch (e: IllegalArgumentException) {
                CONSULTA
            }
        }
    }
}