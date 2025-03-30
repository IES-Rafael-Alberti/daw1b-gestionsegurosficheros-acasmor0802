package org.example.model

import java.time.LocalDate
import java.time.Period

class SeguroVida private constructor(
    numPoliza: Int,
    dniTitular: String,
    importe: Double,
    private val fechaNac: LocalDate,
    private val nivelRiesgo: Riesgo,
    private val indemnizacion: Double
) : Seguro(numPoliza, dniTitular, importe) {

    companion object {
        var numPolizasVida = 800000

        fun crearSeguro(datos: List<String>): SeguroVida {
            return try {
                SeguroVida(
                    datos[0].toInt(),
                    datos[1],
                    datos[2].toDouble(),
                    LocalDate.parse(datos[3]),
                    Riesgo.getRiesgo(datos[4]),
                    datos[5].toDouble()
                )
            } catch (e: Exception) {
                throw IllegalArgumentException("Error al crear seguro de vida: ${e.message}")
            }
        }
    }

    constructor(
        dniTitular: String,
        importe: Double,
        fechaNacimiento: LocalDate,
        nivelRiesgo: Riesgo,
        indemnizacion: Double
    ) : this(
        ++numPolizasVida,
        dniTitular,
        importe,
        fechaNacimiento,
        nivelRiesgo,
        indemnizacion
    )

    override fun calcularImporteAnioSiguiente(interes: Double): Double {
        val edad = Period.between(fechaNac, LocalDate.now()).years
        val incrementoEdad = edad * 0.05
        return importe * (1 + (interes + incrementoEdad + nivelRiesgo.interesAplicado) / 100)
    }

    override fun serializar(separador: String): String {
        return super.serializar(separador) + separador +
                listOf(fechaNac, nivelRiesgo.name, indemnizacion, tipoSeguro())
                    .joinToString(separador)
    }

    override fun toString(): String {
        return super.toString().dropLast(1) +
                ", fechaNac=$fechaNac, nivelRiesgo=${nivelRiesgo.name}, " +
                "indemnizacion=$indemnizacion)"
    }
}