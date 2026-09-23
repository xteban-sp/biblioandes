package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository

/** Detalle de un libro (RF-03). Se observa para ver el stock actualizado tras un préstamo. */
class ObtenerLibroUseCase(private val repositorio: BibliotecaRepository) {
    operator fun invoke(libroId: Int): Flow<Libro?> =
        repositorio.observarLibros().map { libros -> libros.firstOrNull { it.id == libroId } }
}
