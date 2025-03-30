package org.example.model

interface IExportable {
    fun serializar(separador: String = ";"): String
}