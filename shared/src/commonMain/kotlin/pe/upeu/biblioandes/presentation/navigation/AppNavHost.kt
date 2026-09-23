package pe.upeu.biblioandes.presentation.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pe.upeu.biblioandes.presentation.catalogo.CatalogoScreen
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroScreen
import pe.upeu.biblioandes.presentation.inicio.InicioScreen
import pe.upeu.biblioandes.presentation.perfil.PerfilScreen
import pe.upeu.biblioandes.presentation.prestamos.PrestamosScreen

/**
 * Scaffold con barra superior + barra inferior (RF-07) y el grafo de navegación.
 * [oscuro] y [onCambiarTema] solo "pasan" hacia Perfil: el estado vive en App().
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    oscuro: Boolean,
    onCambiarTema: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoActual = entradaActual?.destination
    val enPestanaPrincipal = DestinoInferior.entries.any { destinoActual.es(it.destino) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloDe(destinoActual)) },
                navigationIcon = {
                    // En Detalle y Perfil se muestra la flecha "atrás".
                    if (!enPestanaPrincipal && destinoActual != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                },
                actions = {
                    if (enPestanaPrincipal) {
                        IconButton(onClick = { navController.navigate(Destino.Perfil) { launchSingleTop = true } }) {
                            Icon(Icons.Filled.AccountCircle, contentDescription = "Perfil y ajustes")
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (enPestanaPrincipal) {
                NavigationBar {
                    DestinoInferior.entries.forEach { item ->
                        NavigationBarItem(
                            selected = destinoActual.es(item.destino),
                            onClick = { navController.irAPestana(item.destino) },
                            icon = { Icon(item.icono, contentDescription = null) },
                            label = { Text(item.etiqueta) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destino.Inicio,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            composable<Destino.Inicio> {
                InicioScreen(
                    onIrACatalogo = { navController.irAPestana(Destino.Catalogo) },
                    onIrAPrestamos = { navController.irAPestana(Destino.Prestamos) }
                )
            }
            composable<Destino.Catalogo> {
                CatalogoScreen(onLibroClick = { id -> navController.navigate(Destino.DetalleLibro(id)) })
            }
            composable<Destino.Prestamos> {
                PrestamosScreen()
            }
            composable<Destino.Perfil> {
                PerfilScreen(oscuro = oscuro, onCambiarTema = onCambiarTema)
            }
            composable<Destino.DetalleLibro> { entrada ->
                val ruta: Destino.DetalleLibro = entrada.toRoute()
                DetalleLibroScreen(libroId = ruta.libroId)
            }
        }
    }
}

/**
 * Navegación entre pestañas: vuelve a Inicio en la pila (para que "atrás" desde
 * una pestaña regrese a Inicio y luego salga), evita duplicados y conserva el estado.
 */
private fun NavHostController.irAPestana(destino: Destino) {
    navigate(destino) {
        popUpTo(Destino.Inicio) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavDestination?.es(destino: Destino): Boolean =
    this?.hasRoute(destino::class) == true

private fun tituloDe(destino: NavDestination?): String = when {
    destino.es(Destino.Inicio) -> "BiblioAndes"
    destino.es(Destino.Catalogo) -> "Catálogo"
    destino.es(Destino.Prestamos) -> "Mis préstamos"
    destino.es(Destino.Perfil) -> "Perfil y ajustes"
    destino?.hasRoute(Destino.DetalleLibro::class) == true -> "Detalle del libro"
    else -> "BiblioAndes"
}
