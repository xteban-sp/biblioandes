package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.FiltroEstado
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Préstamos del estudiante (RF-04):
 * 1. aplica RN-03 (si la fecha límite pasó, el préstamo es Vencido),
 * 2. ordena por fecha de devolución más próxima.
 */
class ObtenerPrestamosUseCase(
    private val repositorio: BibliotecaRepository,
    private val reloj: Reloj
) {

    operator fun invoke(): Flow<List<Prestamo>> =
        repositorio.observarPrestamos().map { prestamos -> prepararLista(prestamos) }

    /** Versión puntual (sin observar), usada por otros casos de uso. */
    suspend fun actuales(): List<Prestamo> = prepararLista(repositorio.obtenerPrestamos())

    fun filtrar(prestamos: List<Prestamo>, filtro: FiltroEstado): List<Prestamo> =
        prestamos.filter { filtro.admite(it.estado) }

    private fun prepararLista(prestamos: List<Prestamo>): List<Prestamo> {
        val hoy = reloj.hoy()
        val conEstado = prestamos.map { ReglasPrestamo.actualizarEstado(it, hoy) }
        // Primero los pendientes (Vencido/Activo) por fecha límite más próxima;
        // al final los ya devueltos, del más reciente al más antiguo.
        val (devueltos, pendientes) = conEstado.partition { it.estado is EstadoPrestamo.Devuelto }
        return pendientes.sortedBy { it.fechaLimite } + devueltos.sortedByDescending { it.fechaLimite }
    }
}
