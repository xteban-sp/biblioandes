package pe.upeu.biblioandes.domain.usecase

import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Registra la devolución de un préstamo con la fecha de hoy.
 * Sirve para "regularizar" un préstamo Vencido (RN-04) y así poder volver a pedir libros.
 */
class DevolverPrestamoUseCase(
    private val repositorio: BibliotecaRepository,
    private val reloj: Reloj
) {
    suspend operator fun invoke(prestamoId: Int) =
        repositorio.registrarDevolucion(prestamoId, reloj.hoy())
}
