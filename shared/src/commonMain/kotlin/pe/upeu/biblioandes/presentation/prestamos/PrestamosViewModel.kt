package pe.upeu.biblioandes.presentation.prestamos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.FiltroEstado
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase

data class PrestamosUiState(
    val cargando: Boolean = true,
    val error: String? = null,
    val filtro: FiltroEstado = FiltroEstado.TODOS,
    /** Préstamos ya ordenados (dominio) y filtrados por estado. */
    val prestamos: List<Prestamo> = emptyList(),
    /** Cuántos préstamos hay en cada opción del filtro, para mostrarlo en los chips. */
    val conteos: Map<FiltroEstado, Int> = emptyMap()
) {
    val vacio: Boolean get() = !cargando && error == null && prestamos.isEmpty()
}

class PrestamosViewModel(
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val devolverPrestamo: DevolverPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamosUiState())
    val uiState: StateFlow<PrestamosUiState> = _uiState.asStateFlow()

    private var todos: List<Prestamo> = emptyList()

    init {
        viewModelScope.launch {
            obtenerPrestamos()
                .catch { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
                .collect { lista ->
                    todos = lista
                    val conteos = FiltroEstado.entries.associateWith { obtenerPrestamos.filtrar(lista, it).size }
                    _uiState.update { it.copy(cargando = false, conteos = conteos) }
                    aplicarFiltro()
                }
        }
    }

    fun onFiltroSeleccionado(filtro: FiltroEstado) {
        _uiState.update { it.copy(filtro = filtro) }
        aplicarFiltro()
    }

    fun onDevolver(prestamoId: Int) {
        viewModelScope.launch { devolverPrestamo(prestamoId) }
    }

    private fun aplicarFiltro() {
        _uiState.update { it.copy(prestamos = obtenerPrestamos.filtrar(todos, it.filtro)) }
    }
}
