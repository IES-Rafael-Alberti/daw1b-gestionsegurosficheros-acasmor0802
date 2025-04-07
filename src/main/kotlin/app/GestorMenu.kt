package org.example.app

import org.example.model.Perfil
import org.example.model.Seguro
import org.example.service.IServSeguros
import org.example.service.IServUsuarios
import org.example.ui.IEntradaSalida
import org.example.model.Riesgo
import org.example.model.Cobertura
import org.example.model.Auto

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class GestorMenu(
    val nombreUsuario: String,
    val perfilUsuario: Perfil,
    private val ui: IEntradaSalida,
    private val gestorUsuarios: IServUsuarios,
    private val gestorSeguros: IServSeguros
) {
    fun iniciarMenu(indice: Int = 0) {
        val (opciones, acciones) = ConfiguracionesApp.obtenerMenuYAcciones(perfilUsuario.name, indice)
        ejecutarMenu(opciones, acciones)
    }

    private fun formatearMenu(opciones: List<String>): String {
        return opciones.mapIndexed { index, opcion -> "${index + 1}. $opcion" }.joinToString("\n")
    }

    private fun mostrarMenu(opciones: List<String>) {
        ui.limpiarPantalla()
        ui.mostrar(formatearMenu(opciones), salto = false)
    }

    private fun ejecutarMenu(opciones: List<String>, ejecutar: Map<Int, (GestorMenu) -> Boolean>) {
        do {
            mostrarMenu(opciones)
            val opcion = ui.pedirEntero(
                "Elige opción > ",
                "Opción no válida",
                "Debe ser un número entre 1 y ${opciones.size}"
            ) { it in 1..opciones.size }

            val accion = ejecutar[opcion]
            if (accion != null && accion(this)) return
        } while (true)
    }

    fun nuevoUsuario() {
        ui.limpiarPantalla()
        ui.mostrar("NUEVO USUARIO", pausa = true)

        val nombre = ui.pedirInfo("Nombre de usuario: ", "Nombre no válido") { it.isNotBlank() }
        if (gestorUsuarios.buscarUsuario(nombre) != null) {
            ui.mostrarError("El usuario ya existe")
            return
        }

        val clave = ui.pedirInfoOculta("Contraseña: ")
        if (clave.isBlank()) {
            ui.mostrarError("La contraseña no puede estar vacía")
            return
        }

        val perfil = when (ui.pedirEntero(
            "Perfil (1-ADMIN, 2-GESTION, 3-CONSULTA): ",
            "Opción no válida",
            "Debe ser 1, 2 o 3"
        ) { it in 1..3 }) {
            1 -> Perfil.ADMIN
            2 -> Perfil.GESTION
            else -> Perfil.CONSULTA
        }

        if (gestorUsuarios.agregarUsuario(nombre, clave, perfil)) {
            ui.mostrar("Usuario creado con éxito", pausa = true)
        } else {
            ui.mostrarError("No se pudo crear el usuario")
        }
    }

    fun eliminarUsuario() {
        ui.limpiarPantalla()
        ui.mostrar("ELIMINAR USUARIO", pausa = true)

        val nombre = ui.pedirInfo("Nombre de usuario a eliminar: ")
        val usuario = gestorUsuarios.buscarUsuario(nombre)

        if (usuario == null) {
            ui.mostrarError("Usuario no encontrado")
            return
        }

        if (usuario.nombre == nombreUsuario) {
            ui.mostrarError("No puedes eliminarte a ti mismo")
            return
        }

        if (ui.preguntar("¿Estás seguro de eliminar al usuario ${usuario.nombre}?")) {
            if (gestorUsuarios.eliminarUsuario(nombre)) {
                ui.mostrar("Usuario eliminado con éxito", pausa = true)
            } else {
                ui.mostrarError("No se pudo eliminar el usuario")
            }
        }
    }

    fun cambiarClaveUsuario() {
        ui.limpiarPantalla()
        ui.mostrar("CAMBIAR CONTRASEÑA", pausa = true)

        val usuario = gestorUsuarios.buscarUsuario(nombreUsuario) ?: run {
            ui.mostrarError("Usuario no encontrado")
            return
        }

        val claveActual = ui.pedirInfoOculta("Contraseña actual: ")
        val perfilGestorUsuario = gestorUsuarios.iniciarSesion(nombreUsuario, claveActual)

        if (perfilGestorUsuario == null) {
            ui.mostrarError("Clave actual incorrecta")
            return
        }

        val nuevaClave = ui.pedirInfoOculta("Nueva contraseña: ")
        if (nuevaClave.isBlank()) {
            ui.mostrarError("La contraseña no puede estar vacía")
            return
        }

        val confirmacion = ui.pedirInfoOculta("Confirma la nueva contraseña: ")
        if (nuevaClave != confirmacion) {
            ui.mostrarError("Las contraseñas no coinciden")
            return
        }

        if (gestorUsuarios.cambiarClave(usuario, nuevaClave)) {
            ui.mostrar("Contraseña cambiada con éxito", pausa = true)
        } else {
            ui.mostrarError("No se pudo cambiar la contraseña")
        }
    }

    fun consultarUsuarios() {
        ui.limpiarPantalla()
        ui.mostrar("LISTADO DE USUARIOS", pausa = true)

        val perfil = if (perfilUsuario == Perfil.ADMIN && ui.preguntar("¿Filtrar por perfil?")) {
            when (ui.pedirEntero(
                "Perfil (1-ADMIN, 2-GESTION, 3-CONSULTA): ",
                "Opción no válida",
                "Debe ser 1, 2 o 3"
            ) { it in 1..3 }) {
                1 -> Perfil.ADMIN
                2 -> Perfil.GESTION
                else -> Perfil.CONSULTA
            }
        } else null

        val usuarios = if (perfil != null) {
            gestorUsuarios.consultarPorPerfil(perfil)
        } else {
            gestorUsuarios.consultarTodos()
        }

        if (usuarios.isEmpty()) {
            ui.mostrar("No hay usuarios para mostrar")
        } else {
            usuarios.forEach { ui.mostrar(it.toString()) }
        }
        ui.pausar()
    }

    private fun pedirDni(): String {
        return ui.pedirInfo("DNI del titular (8 números y letra): ", "DNI no válido") { dni ->
            dni.matches(Regex("[0-9]{8}[A-Za-z]"))
        }.uppercase()
    }

    private fun pedirImporte(): Double {
        return ui.pedirDouble(
            "Importe anual del seguro: ",
            "El importe debe ser positivo",
            "Debe ser un número válido"
        ) { it > 0 }
    }

    fun contratarSeguroHogar() {
        ui.limpiarPantalla()
        ui.mostrar("CONTRATAR SEGURO DE HOGAR", pausa = true)

        val dni = pedirDni()
        val importe = pedirImporte()

        val metros = ui.pedirEntero(
            "Metros cuadrados de la vivienda: ",
            "Debe ser un número positivo",
            "Debe ser un número válido"
        ) { it > 0 }

        val valor = ui.pedirDouble(
            "Valor del contenido: ",
            "Debe ser positivo",
            "Debe ser un número válido"
        ) { it > 0 }

        val direccion = ui.pedirInfo("Dirección: ", "La dirección no puede estar vacía") { it.isNotBlank() }

        val anio = ui.pedirEntero(
            "Año de construcción: ",
            "Debe ser un año válido",
            "Debe ser un número válido"
        ) { it in 1800..LocalDate.now().year }

        if (gestorSeguros.contratarSeguroHogar(dni, importe, metros, valor, direccion, anio)) {
            ui.mostrar("Seguro de hogar contratado con éxito", pausa = true)
        } else {
            ui.mostrarError("No se pudo contratar el seguro")
        }
    }

    fun contratarSeguroAuto() {
        ui.limpiarPantalla()
        ui.mostrar("CONTRATAR SEGURO DE AUTO", pausa = true)

        val dni = pedirDni()
        val importe = pedirImporte()
        val descripcion = ui.pedirInfo("Descripción del vehículo: ", "La descripción no puede estar vacía") { it.isNotBlank() }
        val combustible = ui.pedirInfo("Combustible: ", "El combustible no puede estar vacío") { it.isNotBlank() }

        val tipoAuto = when (ui.pedirEntero(
            "Tipo de vehículo (1-Coche, 2-Moto, 3-Camión): ",
            "Opción no válida",
            "Debe ser 1, 2 o 3"
        ) { it in 1..3 }) {
            1 -> Auto.COCHE
            2 -> Auto.MOTO
            else -> Auto.CAMION
        }

        val cobertura = when (ui.pedirEntero(
            "Cobertura (1-Terceros, 2-Terceros+, 3-Franquicia 200, 4-Franquicia 300, 5-Franquicia 400, 6-Franquicia 500, 7-Todo Riesgo): ",
            "Opción no válida",
            "Debe ser entre 1 y 7"
        ) { it in 1..7 }) {
            1 -> Cobertura.TERCEROS
            2 -> Cobertura.TERCEROS_AMPLIADO
            3 -> Cobertura.FRANQUICIA_200
            4 -> Cobertura.FRANQUICIA_300
            5 -> Cobertura.FRANQUICIA_400
            6 -> Cobertura.FRANQUICIA_500
            else -> Cobertura.TODO_RIESGO
        }

        val asistencia = ui.preguntar("¿Incluir asistencia en carretera?")

        val partes = ui.pedirEntero(
            "Número de partes en el último año: ",
            "Debe ser un número positivo",
            "Debe ser un número válido"
        ) { it >= 0 }

        if (gestorSeguros.contratarSeguroAuto(dni, importe, descripcion, combustible, tipoAuto, cobertura, asistencia, partes)) {
            ui.mostrar("Seguro de auto contratado con éxito", pausa = true)
        } else {
            ui.mostrarError("No se pudo contratar el seguro")
        }
    }

    fun contratarSeguroVida() {
        ui.limpiarPantalla()
        ui.mostrar("CONTRATAR SEGURO DE VIDA", pausa = true)

        val dni = pedirDni()
        val importe = pedirImporte()

        val fechaNac = try {
            LocalDate.parse(ui.pedirInfo(
                "Fecha de nacimiento (AAAA-MM-DD): ",
                "Formato de fecha incorrecto"
            ) { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) })
        } catch (e: DateTimeParseException) {
            ui.mostrarError("Fecha no válida")
            return
        }

        val riesgo = when (ui.pedirEntero(
            "Nivel de riesgo (1-Bajo, 2-Medio, 3-Alto): ",
            "Opción no válida",
            "Debe ser 1, 2 o 3"
        ) { it in 1..3 }) {
            1 -> Riesgo.BAJO
            2 -> Riesgo.MEDIO
            else -> Riesgo.ALTO
        }

        val indemnizacion = ui.pedirDouble(
            "Indemnización: ",
            "Debe ser positiva",
            "Debe ser un número válido"
        ) { it > 0 }

        if (gestorSeguros.contratarSeguroVida(dni, importe, fechaNac, riesgo, indemnizacion)) {
            ui.mostrar("Seguro de vida contratado con éxito", pausa = true)
        } else {
            ui.mostrarError("No se pudo contratar el seguro")
        }
    }

    fun eliminarSeguro() {
        ui.limpiarPantalla()
        ui.mostrar("ELIMINAR SEGURO", pausa = true)

        val numPoliza = ui.pedirEntero(
            "Número de póliza a eliminar: ",
            "Debe ser un número positivo",
            "Debe ser un número válido"
        ) { it > 0 }

        val seguro = gestorSeguros.consultarTodos().find { it.numPoliza == numPoliza }
        if (seguro == null) {
            ui.mostrarError("No se encontró un seguro con ese número de póliza")
            return
        }

        ui.mostrar("Seguro a eliminar:\n$seguro")
        if (ui.preguntar("¿Estás seguro de eliminar este seguro?")) {
            if (gestorSeguros.eliminarSeguro(numPoliza)) {
                ui.mostrar("Seguro eliminado con éxito", pausa = true)
            } else {
                ui.mostrarError("No se pudo eliminar el seguro")
            }
        }
    }

    fun consultarSeguros() {
        mostrarSeguros(gestorSeguros.consultarTodos(), "TODOS LOS SEGUROS")
    }

    fun consultarSegurosHogar() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroHogar"), "SEGUROS DE HOGAR")
    }

    fun consultarSegurosAuto() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroAuto"), "SEGUROS DE AUTO")
    }

    fun consultarSegurosVida() {
        mostrarSeguros(gestorSeguros.consultarPorTipo("SeguroVida"), "SEGUROS DE VIDA")
    }

    private fun mostrarSeguros(seguros: List<Seguro>, titulo: String) {
        ui.limpiarPantalla()
        ui.mostrar(titulo, pausa = true)

        if (seguros.isEmpty()) {
            ui.mostrar("No hay seguros para mostrar")
        } else {
            seguros.forEach { ui.mostrar(it.toString()) }
        }
        ui.pausar()
    }
}