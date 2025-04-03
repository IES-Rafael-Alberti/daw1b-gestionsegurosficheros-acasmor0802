package org.example.service

import org.example.data.IRepoUsuarios
import org.example.model.Perfil
import org.example.model.Usuario
import org.example.utils.IUtilSeguridad

class GestorUsuarios(
    private val repoUsuarios: IRepoUsuarios,
    private val seguridad: IUtilSeguridad
) : IServUsuarios, IUtilSeguridad by seguridad {

    override fun iniciarSesion(nombre: String, clave: String): Perfil? {
        val usuario = buscarUsuario(nombre)
        return if (usuario != null && verificarClave(clave, usuario.serializar().split(";")[1])) {
            usuario.perfil
        } else null
    }

    override fun agregarUsuario(nombre: String, clave: String, perfil: Perfil): Boolean {
        val claveEncriptada = encriptarClave(clave)
        return repoUsuarios.agregar(Usuario(nombre, claveEncriptada, perfil))
    }

    override fun eliminarUsuario(nombre: String): Boolean {
        return repoUsuarios.eliminar(nombre)
    }

    override fun cambiarClave(usuario: Usuario, nuevaClave: String): Boolean {
        val nuevaClaveEncriptada = encriptarClave(nuevaClave)
        return repoUsuarios.cambiarClave(usuario, nuevaClaveEncriptada)
    }

    override fun buscarUsuario(nombre: String): Usuario? {
        return repoUsuarios.buscar(nombre)
    }

    override fun consultarTodos(): List<Usuario> {
        return repoUsuarios.obtenerTodos()
    }

    override fun consultarPorPerfil(perfil: Perfil): List<Usuario> {
        return repoUsuarios.obtener(perfil)
    }
}