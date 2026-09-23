package pe.upeu.biblioandes.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo

/** Anima de 0 al valor final la primera vez que se dibuja. */
@Composable
private fun progresoAnimado(objetivo: Float): Float {
    var inicio by remember { mutableStateOf(0f) }
    LaunchedEffect(objetivo) { inicio = objetivo }
    val valor by animateFloatAsState(inicio, animationSpec = tween(durationMillis = 700))
    return valor
}

/** Barra de progreso redondeada y reutilizable. [progreso] va de 0 a 1. */
@Composable
fun BarraProgreso(
    progreso: Float,
    color: Color,
    modifier: Modifier = Modifier,
    fondo: Color = MaterialTheme.colorScheme.surfaceVariant,
    alto: Dp = 8.dp
) {
    val valor = progresoAnimado(progreso.coerceIn(0f, 1f))
    Box(modifier.fillMaxWidth().height(alto).clip(CircleShape).background(fondo)) {
        Box(Modifier.fillMaxWidth(valor).fillMaxHeight().clip(CircleShape).background(color))
    }
}

/**
 * Qué tanto del plazo de 7 días (RN-03) se ha consumido.
 * Activo: días transcurridos / 7. Vencido y Devuelto: barra llena.
 */
fun progresoDevolucion(estado: EstadoPrestamo): Float = when (estado) {
    is EstadoPrestamo.Activo -> {
        val dias = ReglasPrestamo.DIAS_DE_PRESTAMO
        (dias - estado.diasRestantes).coerceIn(0, dias).toFloat() / dias
    }
    is EstadoPrestamo.Vencido -> 1f
    is EstadoPrestamo.Devuelto -> 1f
}

/** Color de la barra: azul normal, ocre si faltan 2 días o menos, rojo si venció. */
@Composable
fun colorDevolucion(estado: EstadoPrestamo): Color = when (estado) {
    is EstadoPrestamo.Activo ->
        if (estado.diasRestantes <= 2) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
    is EstadoPrestamo.Vencido -> MaterialTheme.colorScheme.error
    is EstadoPrestamo.Devuelto -> MaterialTheme.colorScheme.outline
}

/** Barra de devolución con sus textos: "Día 2 de 7" y "Faltan 5 días". */
@Composable
fun BarraDevolucion(prestamo: Prestamo, modifier: Modifier = Modifier) {
    val estado = prestamo.estado
    val dias = ReglasPrestamo.DIAS_DE_PRESTAMO
    val izquierda = when (estado) {
        is EstadoPrestamo.Activo -> "Día ${(dias - estado.diasRestantes).coerceIn(0, dias)} de $dias"
        is EstadoPrestamo.Vencido -> "Plazo de $dias días cumplido"
        is EstadoPrestamo.Devuelto -> "Préstamo cerrado"
    }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        BarraProgreso(progresoDevolucion(estado), colorDevolucion(estado))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(izquierda, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                detalleEstado(estado),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorDevolucion(estado)
            )
        }
    }
}

/** Anillo de progreso (por ejemplo, "2 de 3 préstamos activos"). */
@Composable
fun AnilloProgreso(
    progreso: Float,
    color: Color,
    fondo: Color,
    modifier: Modifier = Modifier,
    tamano: Dp = 72.dp,
    grosor: Dp = 8.dp,
    contenido: @Composable () -> Unit
) {
    val valor = progresoAnimado(progreso.coerceIn(0f, 1f))
    Box(modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(tamano)) {
            val trazo = grosor.toPx()
            val lado = size.minDimension - trazo
            val esquina = Offset(trazo / 2, trazo / 2)
            drawArc(fondo, -90f, 360f, false, esquina, Size(lado, lado), style = Stroke(trazo))
            drawArc(color, -90f, 360f * valor, false, esquina, Size(lado, lado), style = Stroke(trazo, cap = StrokeCap.Round))
        }
        contenido()
    }
}
