package pe.upeu.biblioandes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import pe.upeu.biblioandes.presentation.navigation.AppNavHost
import pe.upeu.biblioandes.presentation.theme.BiblioAndesTheme

/**
 * Raíz de la interfaz, compartida por Android (MainActivity) e iOS (MainViewController).
 *
 * El modo oscuro vive AQUÍ, en la cima del árbol de composición: al cambiarlo,
 * BiblioAndesTheme recibe otro esquema de colores y TODA la app se recompone al instante.
 */
@Composable
fun App() {
    val sistemaOscuro = isSystemInDarkTheme()
    var oscuro by rememberSaveable { mutableStateOf(sistemaOscuro) }

    BiblioAndesTheme(oscuro = oscuro) {
        AppNavHost(oscuro = oscuro, onCambiarTema = { oscuro = it })
    }
}
