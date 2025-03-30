package org.example.data

import org.example.model.Usuario
import org.example.utils.IUtilFicheros

class RepoUsuariosFich(
    private val rutaArchivo: String,
    private val fich: IUtilFicheros
) : RepoUsuariosMem(), ICargarUsuariosIniciales {

    override fun agregar(usuario: Usuario): Boolean {
        if (buscar(usuario.nombre) != null) return false
        return if (fich.agregarLinea(rutaArchivo, usuario.serializar())) {
            super.agregar(usuario)
        } else false
    }

    override fun eliminar(usuario: Usuario): Boolean {
        val usuariosActualizados = obtenerTodos().filter { it != usuario }
        return if (fich.escribirArchivo<Usuario>(rutaArchivo, usuariosActualizados)) {
            super.eliminar(usuario)
        } else false
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        usuario.cambiarClave(nuevaClave)
        return fich.escribirArchivo<Usuario>(rutaArchivo, usuarios)
    }

    override fun cargarUsuarios(): Boolean {
        val lineas = fich.leerArchivo(rutaArchivo)
        if (lineas.isEmpty()) return false

        lineas.forEach { linea ->
            val datos = linea.split(";")
            try {
                val usuario = Usuario.crearUsuario(datos)
                super.agregar(usuario)
            } catch (e: IllegalArgumentException) {
                println("Error al cargar usuario: ${e.message}")
            }
        }
        return true
    }
}