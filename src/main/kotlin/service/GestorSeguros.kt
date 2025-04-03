package org.example.service

import org.example.data.IRepoSeguros
import org.example.model.*
import java.time.LocalDate

class GestorSeguros(
    private val repoSeguros: IRepoSeguros
) : IServSeguros {

    override fun contratarSeguroHogar(
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Int,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: Int
    ): Boolean {
        val seguro = SeguroHogar(
            dniTitular,
            importe,
            metrosCuadrados,
            valorContenido,
            direccion,
            anioConstruccion
        )
        return repoSeguros.agregar(seguro)
    }

    override fun contratarSeguroAuto(
        dniTitular: String,
        importe: Double,
        descripcion: String,
        combustible: String,
        tipoAuto: Auto,
        cobertura: Cobertura,
        asistenciaCarretera: Boolean,
        numPartes: Int
    ): Boolean {
        val seguro = SeguroAuto(
            dniTitular,
            importe,
            descripcion,
            combustible,
            tipoAuto,
            cobertura,
            asistenciaCarretera,
            numPartes
        )
        return repoSeguros.agregar(seguro)
    }

    override fun contratarSeguroVida(
        dniTitular: String,
        importe: Double,
        fechaNacimiento: LocalDate,
        nivelRiesgo: Riesgo,
        indemnizacion: Double
    ): Boolean {
        val seguro = SeguroVida(
            dniTitular,
            importe,
            fechaNacimiento,
            nivelRiesgo,
            indemnizacion
        )
        return repoSeguros.agregar(seguro)
    }

    override fun eliminarSeguro(numPoliza: Int): Boolean {
        return repoSeguros.eliminar(numPoliza)
    }

    override fun consultarTodos(): List<Seguro> {
        return repoSeguros.obtenerTodos()
    }

    override fun consultarPorTipo(tipoSeguro: String): List<Seguro> {
        return repoSeguros.obtener(tipoSeguro)
    }
}