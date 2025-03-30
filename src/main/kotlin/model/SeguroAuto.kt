package org.example.model

class SeguroAuto private constructor(
    numPoliza: Int,
    dniTitular: String,
    importe: Double,
    private val descripcion: String,
    private val combustible: String,
    private val tipoAuto: Auto,
    private val cobertura: Cobertura,
    private val asistenciaCarretera: Boolean,
    private val numPartes: Int
) : Seguro(numPoliza, dniTitular, importe) {

    companion object {
        var numPolizasAuto = 400000
        private const val PORCENTAJE_INCREMENTO_PARTES = 2.0

        fun crearSeguro(datos: List<String>): SeguroAuto {
            return try {
                SeguroAuto(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    datos[3],
                    datos[4],
                    Auto.getAuto(datos[5]),
                    Cobertura.getCobertura(datos[6]),
                    datos[7].toBoolean(),
                    datos[8].toInt()
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Error al crear seguro de auto: ${e.message}")
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        descripcion: String,
        combustible: String,
        tipoAuto: Auto,
        cobertura: Cobertura,
        asistenciaCarretera: Boolean,
        numPartes: Int
    ) : this(
        ++numPolizasAuto,
        dniTitular,
        importe,
        descripcion,
        combustible,
        tipoAuto,
        cobertura,
        asistenciaCarretera,
        numPartes
    )

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val incrementoPartes = numPartes * PORCENTAJE_INCREMENTO_PARTES
        return importe * (1 + (interes + incrementoPartes) / 100)
    }

    override fun serializar(separador: String): String {
        return super.serializar(separador) + separador +
                listOf(descripcion, combustible, tipoAuto.name, cobertura.name,
                    asistenciaCarretera, numPartes, tipoSeguro())
                    .joinToString(separador)
    }

    override fun toString(): String {
        return super.toString().dropLast(1) +
                ", descripcion='$descripcion', combustible='$combustible', " +
                "tipoAuto=${tipoAuto.name}, cobertura=${cobertura.desc}, " +
                "asistenciaCarretera=$asistenciaCarretera, numPartes=$numPartes)"
    }
}