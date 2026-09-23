package pe.upeu.biblioandes.domain.model

/** Préstamo de un libro. Las fechas van en formato ISO "yyyy-MM-dd". */
data class Prestamo(
    val id: Int,
    val libro: Libro,
    val fechaPrestamo: String,
    val fechaLimite: String,
    val estado: EstadoPrestamo
)
