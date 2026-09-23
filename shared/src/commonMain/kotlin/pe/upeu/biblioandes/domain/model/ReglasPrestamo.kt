package pe.upeu.biblioandes.domain.model

import pe.upeu.biblioandes.domain.util.Fechas

/**
 * LAS CUATRO REGLAS DE NEGOCIO VIVEN AQUÍ (capa de dominio).
 * Ninguna pantalla las repite: los casos de uso las invocan y la interfaz
 * solo muestra el resultado.
 */
object ReglasPrestamo {

    /** RN-01: máximo de préstamos Activos simultáneos. */
    const val MAX_PRESTAMOS_ACTIVOS = 3

    /** RN-03: todo préstamo dura siete días. */
    const val DIAS_DE_PRESTAMO = 7

    /** RN-03: la fecha límite es la fecha del préstamo + 7 días. */
    fun calcularFechaLimite(fechaPrestamo: String): String =
        Fechas.sumarDias(fechaPrestamo, DIAS_DE_PRESTAMO)

    /**
     * RN-03: si la fecha límite ya pasó, el préstamo se muestra como Vencido.
     * Recalcula el estado respecto de [hoy]; un préstamo Devuelto no cambia.
     */
    fun actualizarEstado(prestamo: Prestamo, hoy: String): Prestamo {
        if (prestamo.estado is EstadoPrestamo.Devuelto) return prestamo
        val diasRestantes = Fechas.diasEntre(hoy, prestamo.fechaLimite)
        val nuevoEstado = if (diasRestantes >= 0) {
            EstadoPrestamo.Activo(diasRestantes)
        } else {
            EstadoPrestamo.Vencido(-diasRestantes)
        }
        return prestamo.copy(estado = nuevoEstado)
    }

    fun contarActivos(prestamos: List<Prestamo>): Int =
        prestamos.count { it.estado is EstadoPrestamo.Activo }

    fun contarVencidos(prestamos: List<Prestamo>): Int =
        prestamos.count { it.estado is EstadoPrestamo.Vencido }

    /**
     * Decide si el estudiante puede pedir [libro]. Devuelve null si puede,
     * o el motivo por el que no puede. [prestamos] deben tener el estado ya actualizado.
     */
    fun motivoDeRechazo(libro: Libro, prestamos: List<Prestamo>): MotivoRechazo? = when {
        // RN-02: no se puede pedir un libro sin ejemplares.
        libro.ejemplaresDisponibles <= 0 -> MotivoRechazo.SinEjemplares
        // RN-04: con al menos un préstamo Vencido no se puede pedir otro.
        contarVencidos(prestamos) > 0 -> MotivoRechazo.TienePrestamoVencido(contarVencidos(prestamos))
        // RN-01: no más de tres préstamos Activos a la vez.
        contarActivos(prestamos) >= MAX_PRESTAMOS_ACTIVOS -> MotivoRechazo.LimiteDeActivos(MAX_PRESTAMOS_ACTIVOS)
        else -> null
    }
}

/** Motivos por los que se rechaza una solicitud. Cada uno lleva el dato que necesita. */
sealed class MotivoRechazo {
    data object SinEjemplares : MotivoRechazo()                                // RN-02
    data class TienePrestamoVencido(val cantidad: Int) : MotivoRechazo()       // RN-04
    data class LimiteDeActivos(val maximo: Int) : MotivoRechazo()              // RN-01
    data object LibroNoEncontrado : MotivoRechazo()
}

/** Resultado de solicitar un préstamo. */
sealed class ResultadoSolicitud {
    data class Aprobada(val prestamo: Prestamo) : ResultadoSolicitud()
    data class Rechazada(val motivo: MotivoRechazo) : ResultadoSolicitud()
}
