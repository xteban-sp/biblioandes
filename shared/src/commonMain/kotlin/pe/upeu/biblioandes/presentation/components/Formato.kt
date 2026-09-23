package pe.upeu.biblioandes.presentation.components

/** "2026-09-28" -> "28/09/2026". Solo formato de presentación. */
fun String.comoFechaCorta(): String {
    val p = split("-")
    return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else this
}

/** "Diego Huamán Ccama" -> "Diego" */
fun String.primerNombre(): String = trim().substringBefore(" ")
