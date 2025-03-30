package org.example.model

enum class Auto {
    COCHE, MOTO, CAMION;

    companion object {
        fun getAuto(valor: String): Auto {
            return try {
                valueOf(valor.uppercase())
            } catch (e: IllegalArgumentException) {
                COCHE
            }
        }
    }
}