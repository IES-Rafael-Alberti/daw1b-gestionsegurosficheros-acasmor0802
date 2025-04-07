package org.example.ui

interface IEntradaSalida {
    fun mostrar(msj: String, salto: Boolean = true, pausa: Boolean = false)
    fun mostrarError(msj: String, pausa: Boolean = true)
    fun pedirInfo(msj: String = ""): String
    fun pedirInfo(msj: String, error: String, debeCumplir: (String) -> Boolean): String
    fun pedirDouble(prompt: String, error: String, errorConv: String, debeCumplir: (Double) -> Boolean): Double
    fun pedirEntero(prompt: String, error: String, errorConv: String, debeCumplir: (Int) -> Boolean): Int
    fun pedirInfoOculta(prompt: String): String
    fun pausar(msj: String = "Pulse Enter para Continuar...")
    fun limpiarPantalla(numSaltos: Int = 20)
    fun preguntar(mensaje: String): Boolean
}