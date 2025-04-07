package org.example

import org.example.app.CargadorInicial
import org.example.app.ControlAcceso
import org.example.app.GestorMenu
import org.example.data.*
import org.example.service.GestorSeguros
import org.example.service.GestorUsuarios
import org.example.ui.Consola
import org.example.utils.Ficheros
import org.example.utils.Seguridad
import java.io.File

fun main() {
    val rutaUsuarios = "data/Usuarios.txt"
    val rutaSeguros = "data/Seguros.txt"

    val ui = Consola()
    val ficheros = Ficheros(ui)
    val seguridad = Seguridad()

    ui.limpiarPantalla()
    val modoSimulacion = ui.preguntar("¿Desea iniciar en modo simulación? (no se guardarán los datos)")

    val repoUsuarios: IRepoUsuarios
    val repoSeguros: IRepoSeguros

    if (modoSimulacion) {
        repoUsuarios = RepoUsuariosMem()
        repoSeguros = RepoSegurosMem()
    } else {

        if (!ficheros.existeDirectorio("data")) {
            File("data").mkdir()
        }

        repoUsuarios = RepoUsuariosFich(rutaUsuarios, ficheros)
        repoSeguros = RepoSegurosFich(rutaSeguros, ficheros)

        val cargador = CargadorInicial(
            repoUsuarios as ICargarUsuariosIniciales,
            repoSeguros as ICargarSegurosIniciales,
            ui
        )
        cargador.cargarDatosIniciales()
    }

    val gestorUsuarios = GestorUsuarios(repoUsuarios, seguridad)
    val gestorSeguros = GestorSeguros(repoSeguros)

    val controlAcceso = ControlAcceso(rutaUsuarios, ui, gestorUsuarios, ficheros)
    val credenciales = controlAcceso.autenticar() ?: run {
        ui.mostrar("Saliendo del programa...", pausa = true)
        return
    }

    val (nombreUsuario, perfilUsuario) = credenciales
    val gestorMenu = GestorMenu(nombreUsuario, perfilUsuario, ui, gestorUsuarios, gestorSeguros)

    ui.mostrar("Bienvenido, $nombreUsuario (${perfilUsuario.name})", pausa = true)
    gestorMenu.iniciarMenu()

    ui.mostrar("Saliendo del programa...", pausa = true)
}