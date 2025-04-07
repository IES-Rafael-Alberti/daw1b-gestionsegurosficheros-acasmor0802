package org.example.app

import org.example.data.ICargarSegurosIniciales
import org.example.data.ICargarUsuariosIniciales
import org.example.ui.IEntradaSalida

class CargadorInicial(
    private val repoUsuarios: ICargarUsuariosIniciales,
    private val repoSeguros: ICargarSegurosIniciales,
    private val ui: IEntradaSalida
) {
    fun cargarDatosIniciales() {
        if (!repoUsuarios.cargarUsuarios()) {
            ui.mostrarError("No se pudieron cargar los usuarios iniciales")
        }

        if (!repoSeguros.cargarSeguros(ConfiguracionesApp.mapaCrearSeguros)) {
            ui.mostrarError("No se pudieron cargar los seguros iniciales")
        }
    }
}