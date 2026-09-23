package pe.upeu.biblioandes.presentation.prestamos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.FiltroEstado
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.FiltroChips
import pe.upeu.biblioandes.presentation.components.PrestamoItem

/** RF-04: préstamos ordenados por fecha de devolución y filtrables por estado. */
@Composable
fun PrestamosScreen(viewModel: PrestamosViewModel = koinViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    PrestamosContenido(estado, viewModel::onFiltroSeleccionado, viewModel::onDevolver)
}

@Composable
fun PrestamosContenido(
    estado: PrestamosUiState,
    onFiltroSeleccionado: (FiltroEstado) -> Unit,
    onDevolver: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        FiltroChips(
            opciones = FiltroEstado.entries,
            seleccionada = estado.filtro,
            etiqueta = { filtro -> estado.conteos[filtro]?.let { "${filtro.etiqueta} · $it" } ?: filtro.etiqueta },
            onSeleccion = onFiltroSeleccionado,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        when {
            estado.cargando -> EstadoCarga("Cargando tus préstamos…")
            estado.vacio -> EstadoVacio(
                titulo = "Sin préstamos",
                mensaje = if (estado.filtro == FiltroEstado.TODOS) "Aún no tienes préstamos registrados."
                else "No tienes préstamos en estado «${estado.filtro.etiqueta}».",
                icono = Icons.Filled.Inbox
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.prestamos, key = { it.id }) { prestamo ->
                    PrestamoItem(prestamo = prestamo, onDevolver = { onDevolver(prestamo.id) })
                }
            }
        }
    }
}
