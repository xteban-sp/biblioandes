package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * Tarjeta de un préstamo con portada, estado y la barra de devolución.
 * [onDevolver] es opcional: si es null no se muestra el botón.
 */
@Composable
fun PrestamoItem(
    prestamo: Prestamo,
    onDevolver: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                PortadaLibro(prestamo.libro.titulo, prestamo.libro.categoria, ancho = 46.dp, alto = 64.dp)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            prestamo.libro.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        EtiquetaEstado(prestamo.estado)
                    }
                    Text(prestamo.libro.autor, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${prestamo.fechaPrestamo.comoFechaCorta()}  →  ${prestamo.fechaLimite.comoFechaCorta()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            BarraDevolucion(prestamo)
            if (onDevolver != null && prestamo.estado !is EstadoPrestamo.Devuelto) {
                OutlinedButton(onClick = onDevolver, modifier = Modifier.align(Alignment.End)) {
                    Icon(Icons.AutoMirrored.Filled.AssignmentReturn, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Registrar devolución")
                }
            }
        }
    }
}

/** Texto según el estado. El `when` es exhaustivo gracias a la sealed class. */
fun detalleEstado(estado: EstadoPrestamo): String = when (estado) {
    is EstadoPrestamo.Activo ->
        if (estado.diasRestantes == 0) "Vence hoy" else "Faltan ${estado.diasRestantes} día(s)"
    is EstadoPrestamo.Devuelto -> "Devuelto el ${estado.fechaDevolucion.comoFechaCorta()}"
    is EstadoPrestamo.Vencido -> "${estado.diasDeAtraso} día(s) de atraso"
}

/** Etiqueta tipo "píldora" con el nombre del estado. */
@Composable
fun EtiquetaEstado(estado: EstadoPrestamo, modifier: Modifier = Modifier) {
    val (texto, fondo, contenido) = when (estado) {
        is EstadoPrestamo.Activo -> Triple("Activo", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        is EstadoPrestamo.Devuelto -> Triple("Devuelto", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        is EstadoPrestamo.Vencido -> Triple("Vencido", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.onErrorContainer)
    }
    Surface(color = fondo, contentColor = contenido, shape = MaterialTheme.shapes.extraLarge, modifier = modifier) {
        Text(texto, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}
