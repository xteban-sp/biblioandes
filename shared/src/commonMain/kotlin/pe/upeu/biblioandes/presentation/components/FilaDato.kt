package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Fila reutilizable "icono · etiqueta · valor". La usan Detalle y Perfil. */
@Composable
fun FilaDato(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium)
        Text(valor, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}
