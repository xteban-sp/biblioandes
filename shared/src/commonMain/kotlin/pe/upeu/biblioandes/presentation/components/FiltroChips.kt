package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Fila horizontal de chips de filtro, GENÉRICA: sirve para las categorías del
 * catálogo (String?) y para los estados de "Mis préstamos" (FiltroEstado).
 * Recibe las opciones, cuál está elegida y un callback; no guarda estado propio
 * (state hoisting): el ViewModel es quien decide.
 */
@Composable
fun <T> FiltroChips(
    opciones: List<T>,
    seleccionada: T,
    etiqueta: (T) -> String,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier,
    /** Opcional: algo que dibujar antes del texto (por ejemplo, un punto de color). */
    icono: (@Composable (T) -> Unit)? = null
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { opcion ->
            FilterChip(
                selected = opcion == seleccionada,
                onClick = { onSeleccion(opcion) },
                label = { Text(etiqueta(opcion)) },
                leadingIcon = icono?.let { dibujar -> { dibujar(opcion) } }
            )
        }
    }
}
