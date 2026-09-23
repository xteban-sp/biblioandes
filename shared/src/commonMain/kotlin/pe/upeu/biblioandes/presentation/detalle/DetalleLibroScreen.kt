package pe.upeu.biblioandes.presentation.detalle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.presentation.components.PortadaLibro
import pe.upeu.biblioandes.presentation.components.colorDeCategoria
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoVacio

/** RF-03: detalle del libro, acción "Solicitar préstamo" y diálogo de confirmación. */
@Composable
fun DetalleLibroScreen(
    libroId: Int,
    viewModel: DetalleLibroViewModel = koinViewModel(key = "detalle-$libroId") { parametersOf(libroId) }
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    DetalleLibroContenido(
        estado = estado,
        onSolicitar = viewModel::onSolicitarClick,
        onConfirmar = viewModel::onConfirmarSolicitud,
        onCancelar = viewModel::onCancelarConfirmacion,
        onMensajeMostrado = viewModel::onMensajeMostrado
    )
}

@Composable
fun DetalleLibroContenido(
    estado: DetalleLibroUiState,
    onSolicitar: () -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    onMensajeMostrado: () -> Unit
) {
    val libro = estado.libro
    when {
        estado.cargando -> EstadoCarga("Cargando libro…")
        libro == null -> EstadoVacio("Libro no encontrado", "Vuelve al catálogo e inténtalo de nuevo.", Icons.Filled.SearchOff)
        else -> FichaLibro(libro, estado, onSolicitar)
    }

    if (estado.mostrarConfirmacion && libro != null) {
        AlertDialog(
            onDismissRequest = onCancelar,
            title = { Text("Confirmar préstamo") },
            text = { Text("¿Deseas solicitar «${libro.titulo}»? Tendrás 7 días para devolverlo.") },
            confirmButton = { Button(onClick = onConfirmar) { Text("Solicitar") } },
            dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
        )
    }

    estado.mensaje?.let { mensaje ->
        AlertDialog(
            onDismissRequest = onMensajeMostrado,
            title = { Text("Solicitud de préstamo") },
            text = { Text(mensaje) },
            confirmButton = { TextButton(onClick = onMensajeMostrado) { Text("Entendido") } }
        )
    }
}

@Composable
private fun FichaLibro(libro: Libro, estado: DetalleLibroUiState, onSolicitar: () -> Unit) {
    val colorCategoria = colorDeCategoria(libro.categoria)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        // Cabecera con el color de la categoría y la portada grande
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.verticalGradient(listOf(colorCategoria.copy(alpha = 0.35f), Color.Transparent)))
                .padding(top = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            PortadaLibro(libro.titulo, libro.categoria, ancho = 110.dp, alto = 150.dp)
        }
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(libro.titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(libro.autor, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pastilla(Icons.Filled.CalendarMonth, libro.anio.toString())
                Pastilla(Icons.Filled.Category, libro.categoria)
                Pastilla(Icons.Filled.LocationOn, libro.sede)
            }

            TarjetaDisponibilidad(libro.ejemplaresDisponibles)

            estado.motivoRechazo?.let { motivo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.Info, contentDescription = null)
                        Text(motivo.comoMensaje(), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Button(
                onClick = onSolicitar,
                enabled = estado.puedeSolicitar,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (estado.procesando) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text("  Procesando…")
                } else {
                    Icon(Icons.Filled.BookmarkAdd, contentDescription = null)
                    Text("  Solicitar préstamo")
                }
            }
            Text(
                "El préstamo dura ${ReglasPrestamo.DIAS_DE_PRESTAMO} días.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

/** Pastilla pequeña con ícono y texto (año, categoría, sede). */
@Composable
private fun Pastilla(icono: ImageVector, texto: String) {
    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Text(" $texto", style = MaterialTheme.typography.labelMedium)
        }
    }
}

/** Ejemplares disponibles, destacado. */
@Composable
private fun TarjetaDisponibilidad(ejemplares: Int) {
    val hay = ejemplares > 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Icon(
                Icons.Filled.Inventory2, contentDescription = null, modifier = Modifier.size(28.dp),
                tint = if (hay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Column(Modifier.weight(1f)) {
                Text("Ejemplares disponibles", style = MaterialTheme.typography.bodyMedium)
                Text(
                    if (hay) "Listo para préstamo" else "Sin stock por ahora",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "$ejemplares",
                style = MaterialTheme.typography.headlineSmall,
                color = if (hay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}
