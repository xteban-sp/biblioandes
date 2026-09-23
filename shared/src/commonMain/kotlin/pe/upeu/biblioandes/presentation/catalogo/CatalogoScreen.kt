package pe.upeu.biblioandes.presentation.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.upeu.biblioandes.presentation.components.EstadoCarga
import pe.upeu.biblioandes.presentation.components.EstadoError
import pe.upeu.biblioandes.presentation.components.EstadoVacio
import pe.upeu.biblioandes.presentation.components.FiltroChips
import pe.upeu.biblioandes.presentation.components.LibroItem
import pe.upeu.biblioandes.presentation.components.colorDeCategoria

/** RF-02 (catálogo + chips de categoría) y RF-05 (búsqueda). */
@Composable
fun CatalogoScreen(
    onLibroClick: (Int) -> Unit,
    viewModel: CatalogoViewModel = koinViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    CatalogoContenido(
        estado = estado,
        onConsultaCambiada = viewModel::onConsultaCambiada,
        onCategoriaSeleccionada = viewModel::onCategoriaSeleccionada,
        onReintentar = viewModel::cargar,
        onLibroClick = onLibroClick
    )
}

@Composable
fun CatalogoContenido(
    estado: CatalogoUiState,
    onConsultaCambiada: (String) -> Unit,
    onCategoriaSeleccionada: (String?) -> Unit,
    onReintentar: () -> Unit,
    onLibroClick: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = estado.consulta,
            onValueChange = onConsultaCambiada,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar por título o autor") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (estado.consulta.isNotEmpty()) {
                    IconButton(onClick = { onConsultaCambiada("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpiar búsqueda")
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = Color.Transparent
            )
        )

        // null representa "Todas"; así el chip usa el mismo tipo que el filtro.
        FiltroChips(
            opciones = listOf<String?>(null) + estado.categorias,
            seleccionada = estado.categoriaSeleccionada,
            etiqueta = { it ?: "Todas" },
            onSeleccion = onCategoriaSeleccionada,
            modifier = Modifier.padding(bottom = 4.dp),
            icono = { categoria ->
                if (categoria != null) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(colorDeCategoria(categoria)))
                }
            }
        )

        when {
            estado.cargando -> EstadoCarga("Cargando catálogo…")
            estado.error != null -> EstadoError(estado.error, onReintentar)
            estado.vacio -> EstadoVacio(
                titulo = "Sin resultados",
                mensaje = "No hay libros que coincidan con la búsqueda o la categoría elegida.",
                icono = Icons.Filled.SearchOff
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        "${estado.libros.size} libro(s)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(estado.libros, key = { it.id }) { libro ->
                    LibroItem(libro = libro, onClick = { onLibroClick(libro.id) })
                }
            }
        }
    }
}
