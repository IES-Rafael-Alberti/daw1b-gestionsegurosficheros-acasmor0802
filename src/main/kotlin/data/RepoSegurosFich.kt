package org.example.data

import org.example.model.Seguro
import org.example.model.SeguroAuto
import org.example.model.SeguroHogar
import org.example.model.SeguroVida
import org.example.utils.IUtilFicheros

class RepoSegurosFich(
    private val rutaArchivo: String,
    private val fich: IUtilFicheros
) : RepoSegurosMem(), ICargarSegurosIniciales {

    override fun agregar(seguro: Seguro): Boolean {
        return if (fich.agregarLinea(rutaArchivo, seguro.serializar())) {
            super.agregar(seguro)
        } else false
    }

    override fun eliminar(seguro: Seguro): Boolean {
        val segurosActualizados = seguros.filter { it != seguro }
        return if (fich.escribirArchivo(rutaArchivo, segurosActualizados)) {
            super.eliminar(seguro)
        } else false
    }

    override fun cargarSeguros(mapa: Map<String, (List<String>) -> Seguro>): Boolean {
        val lineas = fich.leerArchivo(rutaArchivo)
        if (lineas.isEmpty()) return false

        val segurosCargados = mutableListOf<Seguro>()

        lineas.forEach { linea ->
            val datos = linea.split(";")
            try {
                val tipoSeguro = datos.last()
                val constructor = mapa[tipoSeguro]
                    ?: throw IllegalArgumentException("Tipo de seguro desconocido: $tipoSeguro")

                val seguro = constructor(datos.dropLast(1)) // Eliminamos el tipo del final
                segurosCargados.add(seguro)
            } catch (e: Exception) {
                println("Error al cargar seguro: ${e.message}")
            }
        }

        segurosCargados.forEach { super.agregar(it) }
        actualizarContadores(segurosCargados)
        return true
    }

    private fun actualizarContadores(seguros: List<Seguro>) {
        val maxHogar = seguros.filter { it.tipoSeguro() == "SeguroHogar" }.maxOfOrNull { it.numPoliza }
        val maxAuto = seguros.filter { it.tipoSeguro() == "SeguroAuto" }.maxOfOrNull { it.numPoliza }
        val maxVida = seguros.filter { it.tipoSeguro() == "SeguroVida" }.maxOfOrNull { it.numPoliza }

        if (maxHogar != null) SeguroHogar.numPolizasHogar = maxHogar
        if (maxAuto != null) SeguroAuto.numPolizasAuto = maxAuto
        if (maxVida != null) SeguroVida.numPolizasVida = maxVida
    }
}