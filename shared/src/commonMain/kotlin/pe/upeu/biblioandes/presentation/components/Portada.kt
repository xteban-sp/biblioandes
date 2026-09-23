package pe.upeu.biblioandes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*
 * "Portada" generada para cada libro: un degradado con el color de su categoría
 * y las iniciales del título. No hay imágenes reales porque los datos son simulados.
 */

private val coloresCategoria = listOf(
    Color(0xFF6A4C93), // morado
    Color(0xFF00796B), // verde azulado
    Color(0xFF9C4A2F), // terracota
    Color(0xFF7A5900), // ocre
    Color(0xFF1F4E79), // azul andino
    Color(0xFF2E7D32)  // verde
)

/**
 * Color estable para una categoría, calculado a partir de su nombre.
 * No hay un `when` por categoría: si la biblioteca agrega una sexta,
 * recibe color automáticamente y este archivo no se toca.
 */
fun colorDeCategoria(categoria: String): Color {
    var h = 0L
    for (c in categoria) h = (h * 7 + c.code) % 1_000_003
    return coloresCategoria[(h % coloresCategoria.size).toInt()]
}

/** "Kotlin en profundidad" -> "KP" (primeras letras de las palabras largas). */
fun iniciales(titulo: String): String =
    titulo.split(" ")
        .filter { it.length > 2 }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { titulo.take(1).uppercase() }

@Composable
fun PortadaLibro(
    titulo: String,
    categoria: String,
    modifier: Modifier = Modifier,
    ancho: Dp = 52.dp,
    alto: Dp = 72.dp
) {
    val base = colorDeCategoria(categoria)
    Box(
        modifier = modifier
            .size(ancho, alto)
            .clip(RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 10.dp, bottomEnd = 10.dp))
            .background(Brush.linearGradient(listOf(base, base.copy(alpha = 0.65f)))),
        contentAlignment = Alignment.Center
    ) {
        // Lomo del libro
        Box(
            Modifier.align(Alignment.CenterStart).size(width = ancho * 0.09f, height = alto)
                .background(Color.Black.copy(alpha = 0.18f))
        )
        Text(
            iniciales(titulo),
            color = Color.White,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = (ancho.value * 0.36f).sp
        )
    }
}
