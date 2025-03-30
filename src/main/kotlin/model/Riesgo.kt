package org.example.model

enum class Riesgo(val interesAplicado: Double) {
    BAJO(2.0),
    MEDIO(5.0),
    ALTO(10.0);

    companion object {
        fun getRiesgo(valor: String): Riesgo {
            return try {
                valueOf(valor.uppercase())
            } catch (e: IllegalArgumentException) {
                MEDIO
            }
        }
    }
}