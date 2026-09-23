package pe.upeu.biblioandes.domain.usecase

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.normalizar

/**
 * Catálogo de libros (RF-02) y su filtrado por categoría y búsqueda (RF-05).
 * El filtro vive aquí, en el dominio, y no en el composable.
 */
class ObtenerCatalogoUseCase(private val repositorio: BibliotecaRepository) {

    operator fun invoke(): Flow<List<Libro>> = repositorio.observarLibros()

    suspend fun categorias(): List<String> = repositorio.obtenerCategorias()

    /**
     * @param categoria null = todas las categorías.
     * @param consulta texto buscado en título o autor, sin distinguir mayúsculas ni tildes.
     */
    fun filtrar(libros: List<Libro>, categoria: String?, consulta: String): List<Libro> {
        val texto = consulta.normalizar()
        return libros.filter { libro ->
            val coincideCategoria = categoria == null || libro.categoria == categoria
            val coincideTexto = texto.isEmpty() ||
                libro.titulo.normalizar().contains(texto) ||
                libro.autor.normalizar().contains(texto)
            coincideCategoria && coincideTexto
        }
    }
}
