package pe.upeu.biblioandes.domain.model

/**
 * Estado de un préstamo. Es una sealed class porque cada estado lleva un dato distinto:
 * - Activo   -> cuántos días faltan para devolverlo.
 * - Devuelto -> en qué fecha se devolvió.
 * - Vencido  -> cuántos días de atraso tiene.
 * Con un enum o un String tendríamos que arrastrar propiedades vacías (nulls) en los
 * estados que no las usan. Además, un `when` sobre una sealed class es exhaustivo:
 * si mañana se agrega un estado, el compilador obliga a tratarlo en todas partes.
 */
sealed class EstadoPrestamo {
    data class Activo(val diasRestantes: Int) : EstadoPrestamo()
    data class Devuelto(val fechaDevolucion: String) : EstadoPrestamo()
    data class Vencido(val diasDeAtraso: Int) : EstadoPrestamo()
}
