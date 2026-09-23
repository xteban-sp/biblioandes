package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Rutas TIPADAS de la app. Cada destino es un tipo @Serializable:
 * el compilador impide navegar a una ruta mal escrita o sin su argumento.
 */
@Serializable
sealed interface Destino {
    @Serializable data object Inicio : Destino
    @Serializable data object Catalogo : Destino
    @Serializable data object Prestamos : Destino
    @Serializable data object Perfil : Destino
    /** El detalle necesita saber qué libro mostrar. */
    @Serializable data class DetalleLibro(val libroId: Int) : Destino
}

/** Los tres destinos de la barra inferior (RF-07). */
enum class DestinoInferior(val destino: Destino, val etiqueta: String, val icono: ImageVector) {
    INICIO(Destino.Inicio, "Inicio", Icons.Filled.Home),
    CATALOGO(Destino.Catalogo, "Catálogo", Icons.Filled.LocalLibrary),
    PRESTAMOS(Destino.Prestamos, "Préstamos", Icons.Filled.Bookmarks)
}
