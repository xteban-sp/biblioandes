package pe.upeu.biblioandes.domain.util

/**
 * Normaliza un texto para comparar sin distinguir mayúsculas ni tildes (RF-05).
 * "Cálculo" y "calculo" quedan iguales: "calculo".
 */
fun String.normalizar(): String {
    val sinTildes = buildString {
        for (c in this@normalizar.lowercase()) {
            append(
                when (c) {
                    'á', 'à', 'ä', 'â' -> 'a'
                    'é', 'è', 'ë', 'ê' -> 'e'
                    'í', 'ì', 'ï', 'î' -> 'i'
                    'ó', 'ò', 'ö', 'ô' -> 'o'
                    'ú', 'ù', 'ü', 'û' -> 'u'
                    else -> c // la ñ se conserva: "año" no es "ano"
                }
            )
        }
    }
    return sinTildes.trim()
}
