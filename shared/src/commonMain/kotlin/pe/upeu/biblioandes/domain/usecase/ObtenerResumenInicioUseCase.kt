package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo

/** Lo que necesita la pantalla de Inicio (RF-01). */
data class ResumenInicio(
    val estudiante: Estudiante,
    /** Préstamo Activo cuya devolución vence primero; null si no tiene activos. */
    val proximoAVencer: Prestamo?,
    val activos: Int,
    val vencidos: Int,
    val devueltos: Int
)

class ObtenerResumenInicioUseCase(
    private val obtenerEstudiante: ObtenerEstudianteUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase
) {
    operator fun invoke(): Flow<ResumenInicio> =
        combine(flow { emit(obtenerEstudiante()) }, obtenerPrestamos()) { estudiante, prestamos ->
            ResumenInicio(
                estudiante = estudiante,
                proximoAVencer = prestamos
                    .filter { it.estado is EstadoPrestamo.Activo }
                    .minByOrNull { it.fechaLimite },
                activos = ReglasPrestamo.contarActivos(prestamos),
                vencidos = ReglasPrestamo.contarVencidos(prestamos),
                devueltos = prestamos.count { it.estado is EstadoPrestamo.Devuelto }
            )
        }
}
