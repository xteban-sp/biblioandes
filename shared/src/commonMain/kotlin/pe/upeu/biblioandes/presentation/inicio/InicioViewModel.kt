package pe.upeu.biblioandes.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.usecase.ObtenerResumenInicioUseCase
import pe.upeu.biblioandes.domain.usecase.ResumenInicio

data class InicioUiState(
    val cargando: Boolean = true,
    val resumen: ResumenInicio? = null,
    val error: String? = null
)

class InicioViewModel(
    private val obtenerResumen: ObtenerResumenInicioUseCase
) : ViewModel() {

    // Privado y mutable adentro; público y SOLO LECTURA afuera.
    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        // viewModelScope: la corrutina se cancela sola cuando el ViewModel se destruye.
        viewModelScope.launch {
            obtenerResumen()
                .catch { e -> _uiState.update { it.copy(cargando = false, error = e.message) } }
                .collect { resumen -> _uiState.update { it.copy(cargando = false, resumen = resumen) } }
        }
    }
}
