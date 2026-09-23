package pe.upeu.biblioandes.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val EsquemaClaro = lightColorScheme(
    primary = AzulAndino,
    onPrimary = SobreAzulAndino,
    primaryContainer = AzulAndinoContenedor,
    onPrimaryContainer = SobreAzulAndinoContenedor,
    secondary = Terracota,
    onSecondary = SobreTerracota,
    secondaryContainer = TerracotaContenedor,
    onSecondaryContainer = SobreTerracotaContenedor,
    tertiary = Ocre,
    tertiaryContainer = OcreContenedor,
    onTertiaryContainer = SobreOcreContenedor,
    background = FondoClaro,
    surface = SuperficieClara,
    surfaceVariant = SuperficieVarianteClara,
    onSurface = SobreSuperficieClara,
    onBackground = SobreSuperficieClara,
    error = ErrorClaro,
    errorContainer = ErrorContenedorClaro,
    onSurfaceVariant = SobreVarianteClara,
    outline = BordeClaro,
    outlineVariant = BordeVarianteClaro,
    surfaceContainerLowest = ContenedorMasBajoClaro,
    surfaceContainerLow = ContenedorBajoClaro,
    surfaceContainer = ContenedorClaro,
    surfaceContainerHigh = ContenedorAltoClaro,
    surfaceContainerHighest = ContenedorMasAltoClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulAndinoOscuro,
    onPrimary = SobreAzulAndinoOscuro,
    primaryContainer = AzulAndinoContenedorOscuro,
    onPrimaryContainer = SobreAzulAndinoContenedorOscuro,
    secondary = TerracotaOscuro,
    onSecondary = SobreTerracotaOscuro,
    secondaryContainer = TerracotaContenedorOscuro,
    onSecondaryContainer = SobreTerracotaContenedorOscuro,
    tertiary = OcreOscuro,
    tertiaryContainer = OcreContenedorOscuro,
    onTertiaryContainer = SobreOcreContenedorOscuro,
    background = FondoOscuro,
    surface = SuperficieOscura,
    surfaceVariant = SuperficieVarianteOscura,
    onSurface = SobreSuperficieOscura,
    onBackground = SobreSuperficieOscura,
    error = ErrorOscuro,
    errorContainer = ErrorContenedorOscuro,
    onSurfaceVariant = SobreVarianteOscura,
    outline = BordeOscuro,
    outlineVariant = BordeVarianteOscuro,
    surfaceContainerLowest = ContenedorMasBajoOscuro,
    surfaceContainerLow = ContenedorBajoOscuro,
    surfaceContainer = ContenedorOscuro,
    surfaceContainerHigh = ContenedorAltoOscuro,
    surfaceContainerHighest = ContenedorMasAltoOscuro
)

/** Esquinas más redondeadas que las de Material por defecto: look más amable. */
val BiblioAndesShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Tema Material 3 de BiblioAndes. Recibe [oscuro] desde arriba (state hoisting):
 * quien decide el modo es App(), y al cambiar el valor se recompone toda la app.
 */
@Composable
fun BiblioAndesTheme(
    oscuro: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        typography = BiblioAndesTypography,
        shapes = BiblioAndesShapes,
        content = content
    )
}
