package org.example.app

data class ConfigMenuPerfil(
    val menus: List<List<String>>,
    val acciones: List<Map<Int, (GestorMenu) -> Boolean>>
)