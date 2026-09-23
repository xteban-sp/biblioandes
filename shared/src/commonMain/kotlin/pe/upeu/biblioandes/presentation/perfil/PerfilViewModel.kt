package pe.upeu.biblioandes.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase

data class PerfilUiState(val cargando: Boolean = true, val estudiante: Estudiante? = null)

class PerfilViewModel(private val obtenerEstudiante: ObtenerEstudianteUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { _uiState.value = PerfilUiState(cargando = false, estudiante = obtenerEstudiante()) }
    }
}
