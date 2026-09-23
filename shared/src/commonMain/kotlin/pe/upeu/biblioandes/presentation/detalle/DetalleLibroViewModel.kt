package pe.upeu.biblioandes.presentation.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.MotivoRechazo
import pe.upeu.biblioandes.domain.model.ResultadoSolicitud
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.presentation.components.comoFechaCorta

data class DetalleLibroUiState(
    val cargando: Boolean = true,
    val libro: Libro? = null,
    /** Si no es null, el botón "Solicitar préstamo" se deshabilita y se muestra el motivo. */
    val motivoRechazo: MotivoRechazo? = null,
    val mostrarConfirmacion: Boolean = false,
    val procesando: Boolean = false,
    /** Mensaje del resultado de la solicitud (éxito o rechazo). */
    val mensaje: String? = null
) {
    val puedeSolicitar: Boolean get() = libro != null && motivoRechazo == null && !procesando
}

class DetalleLibroViewModel(
    private val libroId: Int,
    private val obtenerLibro: ObtenerLibroUseCase,
    private val obtenerPrestamos: ObtenerPrestamosUseCase,
    private val solicitarPrestamo: SolicitarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleLibroUiState())
    val uiState: StateFlow<DetalleLibroUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Se combinan el libro y los préstamos: si cambia el stock o se registra
            // un préstamo, el motivo de rechazo se recalcula solo (en el DOMINIO).
            combine(obtenerLibro(libroId), obtenerPrestamos()) { libro, prestamos ->
                libro to libro?.let { solicitarPrestamo.motivoDeRechazo(it, prestamos) }
            }
                .catch { e -> _uiState.update { it.copy(cargando = false, mensaje = e.message) } }
                .collect { (libro, motivo) ->
                    _uiState.update { it.copy(cargando = false, libro = libro, motivoRechazo = motivo) }
                }
        }
    }

    fun onSolicitarClick() = _uiState.update { it.copy(mostrarConfirmacion = true) }

    fun onCancelarConfirmacion() = _uiState.update { it.copy(mostrarConfirmacion = false) }

    fun onConfirmarSolicitud() {
        _uiState.update { it.copy(mostrarConfirmacion = false, procesando = true) }
        viewModelScope.launch {
            val mensaje = when (val resultado = solicitarPrestamo(libroId)) {
                is ResultadoSolicitud.Aprobada ->
                    "¡Préstamo registrado! Devuélvelo hasta el ${resultado.prestamo.fechaLimite.comoFechaCorta()}."
                is ResultadoSolicitud.Rechazada -> resultado.motivo.comoMensaje()
            }
            _uiState.update { it.copy(procesando = false, mensaje = mensaje) }
        }
    }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }
}

/** Traduce el motivo del dominio a un texto para el usuario (eso sí es de presentación). */
fun MotivoRechazo.comoMensaje(): String = when (this) {
    MotivoRechazo.SinEjemplares -> "No hay ejemplares disponibles de este libro."
    is MotivoRechazo.TienePrestamoVencido ->
        "Tienes $cantidad préstamo(s) vencido(s). Regularízalo antes de pedir otro libro."
    is MotivoRechazo.LimiteDeActivos -> "Ya tienes $maximo préstamos activos, que es el máximo permitido."
    MotivoRechazo.LibroNoEncontrado -> "El libro ya no existe en el catálogo."
}
