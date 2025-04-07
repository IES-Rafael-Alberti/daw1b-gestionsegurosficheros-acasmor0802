package org.example.app

import org.example.model.Seguro
import org.example.model.SeguroAuto
import org.example.model.SeguroHogar
import org.example.model.SeguroVida

object ConfiguracionesApp {
    val mapaCrearSeguros: Map<String, (List<String>) -> Seguro> = mapOf(
        "SeguroHogar" to SeguroHogar::crearSeguro,
        "SeguroAuto" to SeguroAuto::crearSeguro,
        "SeguroVida" to SeguroVida::crearSeguro
    )

    private val menusAccionesPorPerfil: Map<String, ConfigMenuPerfil> = mapOf(
        "ADMIN" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Usuarios", "Seguros", "Salir"),
                listOf("Nuevo", "Eliminar", "Cambiar contraseña", "Consultar", "Volver"),
                listOf("Contratar", "Eliminar", "Consultar", "Volver"),
                listOf("Hogar", "Auto", "Vida", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { it.iniciarMenu(2); false },
                    3 to { true }
                ),
                mapOf(
                    1 to { it.nuevoUsuario(); false },
                    2 to { it.eliminarUsuario(); false },
                    3 to { it.cambiarClaveUsuario(); false },
                    4 to { it.consultarUsuarios(); false },
                    5 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(3); false },
                    2 to { it.eliminarSeguro(); false },
                    3 to { it.iniciarMenu(4); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.contratarSeguroHogar(); false },
                    2 to { it.contratarSeguroAuto(); false },
                    3 to { it.contratarSeguroVida(); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        ),
        "GESTION" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Seguros", "Salir"),
                listOf("Contratar", "Eliminar", "Consultar", "Volver"),
                listOf("Hogar", "Auto", "Vida", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(2); false },
                    2 to { it.eliminarSeguro(); false },
                    3 to { it.iniciarMenu(3); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.contratarSeguroHogar(); false },
                    2 to { it.contratarSeguroAuto(); false },
                    3 to { it.contratarSeguroVida(); false },
                    4 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        ),
        "CONSULTA" to ConfigMenuPerfil(
            menus = listOf(
                listOf("Seguros", "Salir"),
                listOf("Consultar", "Volver"),
                listOf("Todos", "Hogar", "Auto", "Vida", "Volver")
            ),
            acciones = listOf(
                mapOf(
                    1 to { it.iniciarMenu(1); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.iniciarMenu(2); false },
                    2 to { true }
                ),
                mapOf(
                    1 to { it.consultarSeguros(); false },
                    2 to { it.consultarSegurosHogar(); false },
                    3 to { it.consultarSegurosAuto(); false },
                    4 to { it.consultarSegurosVida(); false },
                    5 to { true }
                )
            )
        )
    )

    fun obtenerMenuYAcciones(perfil: String, indice: Int): Pair<List<String>, Map<Int, (GestorMenu) -> Boolean>> {
        val config = menusAccionesPorPerfil[perfil] ?: return emptyList<String>() to emptyMap()
        val menu = config.menus.getOrNull(indice) ?: emptyList()
        val acciones = config.acciones.getOrNull(indice) ?: emptyMap()
        return menu to acciones
    }
}