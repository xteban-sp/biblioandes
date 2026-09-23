package pe.upeu.biblioandes.presentation.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.usecase.ResumenInicio
import pe.upeu.biblioandes.presentation.components.AnilloProgreso
import pe.upeu.biblioandes.presentation.components.BarraDevolucion
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.PortadaLibro
import pe.upeu.biblioandes.presentation.components.comoFechaCorta
import pe.upeu.biblioandes.presentation.components.primerNombre

/** RF-01. Versión "con ViewModel": obtiene el estado y delega el dibujo. */
@Composable
fun InicioScreen(
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit,
    viewModel: InicioViewModel = koinViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    InicioContenido(estado, onIrACatalogo, onIrAPrestamos)
}

/** Versión "sin estado": solo dibuja lo que recibe. */
@Composable
fun InicioContenido(
    estado: InicioUiState,
    onIrACatalogo: () -> Unit,
    onIrAPrestamos: () -> Unit
) {
    val resumen = estado.resumen
    if (estado.cargando || resumen == null) {
        EstadoCarga("Cargando tu resumen…")
        return
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TarjetaBienvenida(resumen)

        if (resumen.vencidos > 0) AvisoVencidos(resumen.vencidos, onIrAPrestamos)

        Text("Próxima devolución", style = MaterialTheme.typography.titleMedium)
        TarjetaProximoAVencer(resumen.proximoAVencer, onIrAPrestamos)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniDato("Activos", resumen.activos, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            MiniDato("Vencidos", resumen.vencidos, MaterialTheme.colorScheme.error, Modifier.weight(1f))
            MiniDato("Devueltos", resumen.devueltos, MaterialTheme.colorScheme.outline, Modifier.weight(1f))
        }

        Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AccesoRapido(Icons.Filled.LocalLibrary, "Catálogo", "Explora y pide libros", onIrACatalogo, Modifier.weight(1f))
            AccesoRapido(Icons.Filled.Bookmarks, "Mis préstamos", "Revisa tus fechas", onIrAPrestamos, Modifier.weight(1f))
        }
    }
}

/** Cabecera con degradado, saludo y anillo "X de 3 activos" (RN-01). */
@Composable
private fun TarjetaBienvenida(resumen: ResumenInicio) {
    val esquema = MaterialTheme.colorScheme
    val maximo = ReglasPrestamo.MAX_PRESTAMOS_ACTIVOS
    Box(
        Modifier.fillMaxWidth().clip(MaterialTheme.shapes.large)
            .background(Brush.linearGradient(listOf(esquema.primary, esquema.secondary)))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("¡Hola, ${resumen.estudiante.nombre.primerNombre()}! 👋", style = MaterialTheme.typography.headlineSmall, color = esquema.onPrimary)
                Text(resumen.estudiante.carrera, style = MaterialTheme.typography.bodyMedium, color = esquema.onPrimary.copy(alpha = 0.85f))
                Text(
                    if (resumen.activos >= maximo) "Llegaste al máximo de préstamos." else "Puedes pedir ${maximo - resumen.activos} libro(s) más.",
                    style = MaterialTheme.typography.labelLarge,
                    color = esquema.onPrimary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            AnilloProgreso(
                progreso = resumen.activos.toFloat() / maximo,
                color = esquema.onPrimary,
                fondo = esquema.onPrimary.copy(alpha = 0.25f),
                tamano = 84.dp
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${resumen.activos}/$maximo", style = MaterialTheme.typography.titleLarge, color = esquema.onPrimary)
                    Text("activos", style = MaterialTheme.typography.labelSmall, color = esquema.onPrimary)
                }
            }
        }
    }
}

/** Tarjeta destacada con el préstamo cuya devolución vence primero + barra de progreso. */
@Composable
private fun TarjetaProximoAVencer(prestamo: Prestamo?, onVerPrestamos: () -> Unit) {
    Card(
        onClick = onVerPrestamos,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (prestamo == null) {
            Text("No tienes préstamos activos. ¡Buen momento para pedir un libro!", Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
            return@Card
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                PortadaLibro(prestamo.libro.titulo, prestamo.libro.categoria)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(prestamo.libro.titulo, style = MaterialTheme.typography.titleLarge, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(prestamo.libro.autor, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "Devolver el ${prestamo.fechaLimite.comoFechaCorta()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            BarraDevolucion(prestamo)
        }
    }
}

@Composable
private fun MiniDato(etiqueta: String, valor: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$valor", style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AccesoRapido(icono: ImageVector, titulo: String, subtitulo: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondary)
            }
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
private fun AvisoVencidos(cantidad: Int, onIrAPrestamos: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Filled.Warning, contentDescription = null)
            Column(Modifier.weight(1f)) {
                Text("$cantidad préstamo(s) vencido(s)", style = MaterialTheme.typography.titleSmall)
                Text("Regularízalo para pedir nuevos libros.", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onIrAPrestamos) {
                Text("Ver", color = MaterialTheme.colorScheme.onErrorContainer)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
    }
}
