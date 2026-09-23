package pe.upeu.biblioandes.domain.model

/**
 * Opciones del filtro de "Mis préstamos" (RF-04).
 * OJO: esto NO es el estado del préstamo (ese es la sealed class EstadoPrestamo);
 * es solo la opción que el usuario elige en los chips, por eso aquí sí basta un enum.
 */
enum class FiltroEstado(val etiqueta: String) {
    TODOS("Todos"),
    ACTIVO("Activo"),
    DEVUELTO("Devuelto"),
    VENCIDO("Vencido");

    fun admite(estado: EstadoPrestamo): Boolean = when (this) {
        TODOS -> true
        ACTIVO -> estado is EstadoPrestamo.Activo
        DEVUELTO -> estado is EstadoPrestamo.Devuelto
        VENCIDO -> estado is EstadoPrestamo.Vencido
    }
}
