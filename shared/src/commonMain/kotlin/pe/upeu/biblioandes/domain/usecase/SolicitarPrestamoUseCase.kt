package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.MotivoRechazo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.model.ResultadoSolicitud
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Solicitar un préstamo (RF-03). Antes de registrar aplica RN-01, RN-02 y RN-04.
 * Aunque la pantalla deshabilite el botón, la validación real ocurre aquí:
 * la interfaz nunca es la última línea de defensa.
 */
class SolicitarPrestamoUseCase(
    private val repositorio: BibliotecaRepository,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val reloj: Reloj
) {

    suspend operator fun invoke(libroId: Int): ResultadoSolicitud {
        val libro = repositorio.obtenerLibro(libroId)
            ?: return ResultadoSolicitud.Rechazada(MotivoRechazo.LibroNoEncontrado)

        val prestamos = obtenerPrestamos.actuales()
        val motivo = ReglasPrestamo.motivoDeRechazo(libro, prestamos)
        if (motivo != null) return ResultadoSolicitud.Rechazada(motivo)

        val hoy = reloj.hoy()
        val prestamo = repositorio.registrarPrestamo(
            libroId = libro.id,
            fechaPrestamo = hoy,
            fechaLimite = ReglasPrestamo.calcularFechaLimite(hoy) // RN-03: siete días
        )
        return ResultadoSolicitud.Aprobada(prestamo)
    }

    /** Para que la pantalla sepa, antes de pulsar, si el botón debe estar habilitado. */
    fun motivoDeRechazo(libro: Libro, prestamos: List<Prestamo>): MotivoRechazo? =
        ReglasPrestamo.motivoDeRechazo(libro, prestamos)
}
