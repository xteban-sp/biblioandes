package pe.upeu.biblioandes.domain.util

/**
 * Operaciones con fechas ISO "yyyy-MM-dd" escritas en Kotlin puro (sin librerías),
 * para que el dominio siga siendo multiplataforma y sin dependencias.
 *
 * Convierte una fecha a "día época" (días desde 1970-01-01) y viceversa;
 * así restar dos fechas es restar dos números.
 * Algoritmo de calendario civil de Howard Hinnant (días <-> año/mes/día).
 */
object Fechas {

    fun aDiaEpoch(iso: String): Long {
        val partes = iso.split("-")
        require(partes.size == 3) { "Fecha inválida: $iso (se espera yyyy-MM-dd)" }
        var anio = partes[0].toLong()
        val mes = partes[1].toLong()
        val dia = partes[2].toLong()
        if (mes <= 2) anio -= 1
        val era = anio.floorDiv(400L)
        val anioDeEra = anio - era * 400
        val diaDelAnio = (153 * (mes + if (mes > 2) -3 else 9) + 2) / 5 + dia - 1
        val diaDeEra = anioDeEra * 365 + anioDeEra / 4 - anioDeEra / 100 + diaDelAnio
        return era * 146097 + diaDeEra - 719468
    }

    fun desdeDiaEpoch(diaEpoch: Long): String {
        val z = diaEpoch + 719468
        val era = z.floorDiv(146097L)
        val diaDeEra = z - era * 146097
        val anioDeEra = (diaDeEra - diaDeEra / 1460 + diaDeEra / 36524 - diaDeEra / 146096) / 365
        var anio = anioDeEra + era * 400
        val diaDelAnio = diaDeEra - (365 * anioDeEra + anioDeEra / 4 - anioDeEra / 100)
        val mp = (5 * diaDelAnio + 2) / 153
        val dia = diaDelAnio - (153 * mp + 2) / 5 + 1
        val mes = mp + if (mp < 10) 3 else -9
        if (mes <= 2) anio += 1
        return "${anio.toString().padStart(4, '0')}-${mes.toString().padStart(2, '0')}-${dia.toString().padStart(2, '0')}"
    }

    fun sumarDias(iso: String, dias: Int): String = desdeDiaEpoch(aDiaEpoch(iso) + dias)

    /** Días desde [desde] hasta [hasta]. Positivo si [hasta] es posterior. */
    fun diasEntre(desde: String, hasta: String): Int = (aDiaEpoch(hasta) - aDiaEpoch(desde)).toInt()
}
