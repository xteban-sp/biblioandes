package pe.upeu.biblioandes.domain.model

/**
 * Libro del catálogo, tal como lo entrega la sección 4.2 del caso.
 * No depende de Compose ni de ninguna librería: es Kotlin puro.
 */
data class Libro(
    val id: Int,
    val titulo: String,
    val autor: String,
    val anio: Int,
    val categoria: String,
    val sede: String,
    val ejemplaresDisponibles: Int
)
