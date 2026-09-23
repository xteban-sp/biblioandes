package pe.upeu.biblioandes.domain.util

/**
 * Da la fecha de "hoy". Es una interfaz para que el dominio no dependa del reloj
 * del sistema: en las pruebas se inyecta un reloj fijo y las reglas son verificables.
 */
fun interface Reloj {
    /** Fecha actual en formato "yyyy-MM-dd". */
    fun hoy(): String
}
