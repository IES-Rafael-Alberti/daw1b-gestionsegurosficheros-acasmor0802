package org.example.app

import org.example.model.Perfil
import org.example.service.IServUsuarios
import org.example.ui.IEntradaSalida
import org.example.utils.IUtilFicheros
import java.time.LocalDate

class ControlAcceso(
    private val rutaArchivoUsuarios: String,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val ficheros: IUtilFicheros
) {
    fun autenticar(): Pair<String, Perfil>? {
        if (!verificarFicheroUsuarios()) return null
        return iniciarSesion()
    }

    private fun verificarFicheroUsuarios(): Boolean {
        if (!ficheros.existeFichero(rutaArchivoUsuarios) ||
            ficheros.leerArchivo(rutaArchivoUsuarios).isEmpty()) {

            ui.mostrar("No hay usuarios registrados en el sistema.", pausa = true)
            if (ui.preguntar("¿Desea crear un usuario ADMIN inicial?")) {
                return crearUsuarioAdminInicial()
            }
            return false
        }
        return true
    }

    private fun crearUsuarioAdminInicial(): Boolean {
        ui.mostrar("Creación de usuario ADMIN inicial", pausa = true)

        val nombre = ui.pedirInfo("Nombre de usuario: ", "Nombre no válido") { it.isNotBlank() }
        val clave = ui.pedirInfoOculta("Contraseña: ")

        if (clave.isBlank()) {
            ui.mostrarError("La contraseña no puede estar vacía")
            return false
        }

        return gestorUsuarios.agregarUsuario(nombre, clave, Perfil.ADMIN)
    }

    private fun iniciarSesion(): Pair<String, Perfil>? {
        while (true) {
            ui.limpiarPantalla()
            ui.mostrar("INICIO DE SESIÓN", pausa = true)

            val nombre = ui.pedirInfo("Usuario: ")
            val clave = ui.pedirInfoOculta("Contraseña: ")

            if (nombre.isBlank() || clave.isBlank()) {
                if (ui.preguntar("¿Desea cancelar el inicio de sesión?")) {
                    return null
                }
                continue
            }

            val perfil = gestorUsuarios.iniciarSesion(nombre, clave)
            if (perfil != null) {
                return nombre to perfil
            }

            ui.mostrarError("Credenciales incorrectas")
            if (ui.preguntar("¿Desea intentarlo de nuevo?")) continue else return null
        }
    }
}