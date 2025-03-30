package org.example.model

class Usuario(
    val nombre: String,
    private var clave: String,
    val perfil: Perfil
) : IExportable {

    fun cambiarClave(nuevaClaveEncriptada: String) {
        clave = nuevaClaveEncriptada
    }

    override fun serializar(separador: String): String {
        return listOf(nombre, clave, perfil.name).joinToString(separador)
    }

    companion object {
        fun crearUsuario(datos: List<String>): Usuario {
            return try {
                Usuario(
                    datos[0],
                    datos[1],
                    Perfil.getPerfil(datos[2])
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Error al crear usuario: ${e.message}")
            }
        }
    }

    override fun toString(): String = "Usuario(nombre='$nombre', perfil=${perfil.name})"
}