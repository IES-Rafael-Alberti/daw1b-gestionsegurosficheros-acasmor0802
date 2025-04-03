package org.example.utils

import org.example.model.IExportable
import java.io.File
import java.io.IOException

class Ficheros(private val ui: IEntradaSalida) : IUtilFicheros {

    override fun leerArchivo(ruta: String): List<String> {
        return try {
            File(ruta).takeIf { it.exists() }?.readLines() ?: emptyList()
        } catch (e: IOException) {
            ui.mostrarError("Error al leer el archivo $ruta: ${e.message}")
            emptyList()
        }
    }

    override fun agregarLinea(ruta: String, linea: String): Boolean {
        return try {
            File(ruta).appendText("$linea\n")
            true
        } catch (e: IOException) {
            ui.mostrarError("Error al escribir en el archivo $ruta: ${e.message}")
            false
        }
    }

    override fun <T : IExportable> escribirArchivo(ruta: String, elementos: List<T>): Boolean {
        return try {
            File(ruta).writeText(elementos.joinToString("\n") { it.serializar() })
            true
        } catch (e: IOException) {
            ui.mostrarError("Error al sobrescribir el archivo $ruta: ${e.message}")
            false
        }
    }

    override fun existeFichero(ruta: String): Boolean {
        return File(ruta).isFile
    }

    override fun existeDirectorio(ruta: String): Boolean {
        return File(ruta).isDirectory
    }
}