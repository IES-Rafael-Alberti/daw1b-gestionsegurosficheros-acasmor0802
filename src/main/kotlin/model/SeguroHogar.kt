package org.example.model

import java.time.LocalDate

class SeguroHogar private constructor(
    numPoliza: Int,
    dniTitular: String,
    importe: Double,
    private val metrosCuadrados: Int,
    private val valorContenido: Double,
    private val direccion: String,
    private val anioConstruccion: Int
) : Seguro(numPoliza, dniTitular, importe) {

    companion object {
        var numPolizasHogar = 100000
        private const val PORCENTAJE_INCREMENTO_ANIOS = 0.02
        private const val CICLO_ANIOS_INCREMENTO = 5

        fun crearSeguro(datos: List<String>): SeguroHogar {
            return try {
                SeguroHogar(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    datos[3].toInt(),
                    datos[4].toDouble(),
                    datos[5],
                    datos[6].toInt()
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Error al crear seguro de hogar: ${e.message}")
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        metrosCuadrados: Int,
        valorContenido: Double,
        direccion: String,
        anioConstruccion: Int
    ) : this(
        ++numPolizasHogar,
        dniTitular,
        importe,
        metrosCuadrados,
        valorContenido,
        direccion,
        anioConstruccion
    )

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val antiguedad = LocalDate.now().year - anioConstruccion
        val incrementos = antiguedad / CICLO_ANIOS_INCREMENTO
        val interesResidual = incrementos * PORCENTAJE_INCREMENTO_ANIOS
        return importe * (1 + (interes + interesResidual) / 100)
    }

    override fun serializar(separador: String): String {
        return super.serializar(separador) + separador +
                listOf(metrosCuadrados, valorContenido, direccion, anioConstruccion, tipoSeguro())
                    .joinToString(separador)
    }

    override fun toString(): String {
        return super.toString().dropLast(1) +
                ", metrosCuadrados=$metrosCuadrados, valorContenido=$valorContenido, " +
                "direccion='$direccion', anioConstruccion=$anioConstruccion)"
    }
}