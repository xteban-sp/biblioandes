package pe.upeu.biblioandes.presentation.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase

class CatalogoViewModel(
    private val obtenerCatalogo: ObtenerCatalogoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    /** Catálogo completo tal como llega del caso de uso (sin filtrar). */
    private var todosLosLibros: List<Libro> = emptyList()
    private var carga: Job? = null

    init {
        cargar()
    }

    fun cargar() {
        carga?.cancel()
        carga = viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            val categorias = obtenerCatalogo.categorias()
            obtenerCatalogo()
                .catch { e ->
                    _uiState.update { it.copy(cargando = false, error = e.message ?: "Error desconocido") }
                }
                .collect { libros ->
                    todosLosLibros = libros
                    _uiState.update { it.copy(cargando = false, categorias = categorias) }
                    aplicarFiltros()
                }
        }
    }

    fun onConsultaCambiada(texto: String) {
        _uiState.update { it.copy(consulta = texto) }
        aplicarFiltros()
    }

    fun onCategoriaSeleccionada(categoria: String?) {
        _uiState.update { it.copy(categoriaSeleccionada = categoria) }
        aplicarFiltros()
    }

    /** El ViewModel NO filtra por su cuenta: delega en el caso de uso (dominio). */
    private fun aplicarFiltros() {
        _uiState.update { estado ->
            estado.copy(
                libros = obtenerCatalogo.filtrar(todosLosLibros, estado.categoriaSeleccionada, estado.consulta)
            )
        }
    }
}
