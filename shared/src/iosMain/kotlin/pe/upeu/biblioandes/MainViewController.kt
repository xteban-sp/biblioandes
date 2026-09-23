package pe.upeu.biblioandes

import androidx.compose.ui.window.ComposeUIViewController

/** Punto de entrada de la interfaz en iOS: la misma App() que usa Android. */
fun MainViewController() = ComposeUIViewController { App() }
