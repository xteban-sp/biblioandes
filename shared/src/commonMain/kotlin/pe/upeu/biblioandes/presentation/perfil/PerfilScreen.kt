package pe.upeu.biblioandes.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.FilaDato
import pe.upeu.biblioandes.presentation.components.iniciales

/**
 * RF-06. El modo oscuro NO se guarda aquí: llega como parámetro desde App()
 * (state hoisting). Esta pantalla solo avisa el cambio con [onCambiarTema].
 */
@Composable
fun PerfilScreen(
    oscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    viewModel: PerfilViewModel = koinViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    PerfilContenido(estado, oscuro, onCambiarTema)
}

@Composable
fun PerfilContenido(
    estado: PerfilUiState,
    oscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit
) {
    val estudiante = estado.estudiante
    if (estado.cargando || estudiante == null) {
        EstadoCarga("Cargando perfil…")
        return
    }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val esquema = MaterialTheme.colorScheme
        Box(
            Modifier.size(104.dp).clip(CircleShape)
                .background(Brush.linearGradient(listOf(esquema.primary, esquema.secondary))),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciales(estudiante.nombre), style = MaterialTheme.typography.headlineSmall, color = esquema.onPrimary)
        }
        Text(estudiante.nombre, style = MaterialTheme.typography.headlineSmall)
        Text("Estudiante · ${estudiante.codigo}", style = MaterialTheme.typography.bodyMedium, color = esquema.onSurfaceVariant)
        Text("Mis datos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FilaDato(Icons.Filled.Badge, "Código", estudiante.codigo)
                FilaDato(Icons.Filled.School, "Carrera", estudiante.carrera)
                FilaDato(Icons.Filled.Email, "Correo", estudiante.correo)
            }
        }
        Text("Apariencia", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth())
        Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Filled.DarkMode, contentDescription = null)
                Text("Modo oscuro", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Switch(checked = oscuro, onCheckedChange = onCambiarTema)
            }
        }
    }
}
