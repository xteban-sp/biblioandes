package pe.upeu.biblioandes.presentation.catalogo

import pe.upeu.biblioandes.domain.model.Libro

/**
 * Estado de la pantalla Catálogo. A diferencia del modelo de dominio (Libro),
 * describe lo que la PANTALLA necesita: si carga, si hubo error, qué texto hay
 * en la búsqueda, qué categoría está marcada y qué libros se ven ya filtrados.
 */
data class CatalogoUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val categorias: List<String> = emptyList(),
    /** null = "Todas". */
    val categoriaSeleccionada: String? = null,
    val consulta: String = "",
    /** Libros ya filtrados por categoría y búsqueda. */
    val libros: List<Libro> = emptyList()
) {
    val vacio: Boolean get() = !cargando && error == null && libros.isEmpty()
}
