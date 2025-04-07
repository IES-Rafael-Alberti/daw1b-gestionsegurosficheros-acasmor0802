package org.example.ui

import org.jline.terminal.TerminalBuilder
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.EndOfFileException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Consola : IEntradaSalida {

    override fun mostrar(msj: String, salto: Boolean, pausa: Boolean) {
        if (salto) println(msj) else print(msj)
        if (pausa) pausar()
    }

    override fun mostrarError(msj: String, pausa: Boolean) {
        val mensaje = if (!msj.startsWith("ERROR - ")) "ERROR - $msj" else msj
        mostrar(mensaje, true, pausa)
    }

    override fun pedirInfo(msj: String): String {
        mostrar(msj, false)
        return readln().trim()
    }

    override fun pedirInfo(msj: String, error: String, debeCumplir: (String) -> Boolean): String {
        while (true) {
            val entrada = pedirInfo(msj)
            try {
                require(debeCumplir(entrada)) { error }
                return entrada
            } catch (e: IllegalArgumentException) {
                mostrarError(e.message ?: error)
            }
        }
    }

    override fun pedirDouble(prompt: String, error: String, errorConv: String, debeCumplir: (Double) -> Boolean): Double {
        return pedirInfo(prompt, error) { entrada ->
            entrada.replace(",", ".").toDoubleOrNull()?.let { debeCumplir(it) } ?: false
        }.replace(",", ".").toDouble().also {
            require(debeCumplir(it)) { errorConv }
        }
    }

    override fun pedirEntero(prompt: String, error: String, errorConv: String, debeCumplir: (Int) -> Boolean): Int {
        return pedirInfo(prompt, error) { entrada ->
            entrada.toIntOrNull()?.let { debeCumplir(it) } ?: false
        }.toInt().also {
            require(debeCumplir(it)) { errorConv }
        }
    }

    override fun pedirInfoOculta(prompt: String): String {
        return try {
            val terminal = TerminalBuilder.builder()
                .dumb(true)
                .build()

            val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build()

            reader.readLine(prompt, '*')
        } catch (e: UserInterruptException) {
            mostrarError("Entrada cancelada por el usuario (Ctrl + C).", pausa = false)
            ""
        } catch (e: EndOfFileException) {
            mostrarError("Se alcanzó el final del archivo (EOF ó Ctrl+D).", pausa = false)
            ""
        } catch (e: Exception) {
            mostrarError("Problema al leer la contraseña: ${e.message}", pausa = false)
            ""
        }
    }

    override fun pausar(msj: String) {
        mostrar(msj, true)
        readlnOrNull()
    }

    override fun limpiarPantalla(numSaltos: Int) {
        if (System.console() != null) {
            print("\u001b[H\u001b[2J")
            System.out.flush()
        } else {
            repeat(numSaltos) { println() }
        }
    }

    override fun preguntar(mensaje: String): Boolean {
        while (true) {
            val respuesta = pedirInfo("$mensaje (s/n): ").lowercase()
            when (respuesta) {
                "s" -> return true
                "n" -> return false
                else -> mostrarError("Por favor, introduce 's' o 'n'")
            }
        }
    }
}