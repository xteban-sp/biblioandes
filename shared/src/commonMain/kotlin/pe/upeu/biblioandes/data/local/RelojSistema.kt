package pe.upeu.biblioandes.data.local

import pe.upeu.biblioandes.domain.util.Fechas
import pe.upeu.biblioandes.domain.util.Reloj
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Implementación real del Reloj: lee la hora del sistema (stdlib de Kotlin,
 * funciona igual en Android e iOS) y la lleva a la hora de Lima (UTC-5).
 */
class RelojSistema : Reloj {

    @OptIn(ExperimentalTime::class)
    override fun hoy(): String {
        val segundosLima = Clock.System.now().epochSeconds + DESFASE_LIMA_SEGUNDOS
        return Fechas.desdeDiaEpoch(segundosLima.floorDiv(SEGUNDOS_POR_DIA))
    }

    private companion object {
        const val DESFASE_LIMA_SEGUNDOS = -5L * 60 * 60
        const val SEGUNDOS_POR_DIA = 24L * 60 * 60
    }
}
