package org.example.model

enum class Cobertura(val desc: String) {
    TERCEROS("Terceros"),
    TERCEROS_AMPLIADO("Terceros +"),
    FRANQUICIA_200("Todo Riesgo con Franquicia de 200€"),
    FRANQUICIA_300("Todo Riesgo con Franquicia de 300€"),
    FRANQUICIA_400("Todo Riesgo con Franquicia de 400€"),
    FRANQUICIA_500("Todo Riesgo con Franquicia de 500€"),
    TODO_RIESGO("Todo Riesgo");

    companion object {
        fun getCobertura(valor: String): Cobertura {
            return try {
                valueOf(valor.uppercase())
            } catch (e: IllegalArgumentException) {
                TERCEROS
            }
        }
    }
}