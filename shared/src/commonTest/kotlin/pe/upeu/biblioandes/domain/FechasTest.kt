package pe.upeu.biblioandes.domain

import pe.upeu.biblioandes.domain.util.Fechas
import kotlin.test.Test
import kotlin.test.assertEquals

class FechasTest {
    @Test fun ida_y_vuelta_de_una_fecha() {
        assertEquals("2026-09-23", Fechas.desdeDiaEpoch(Fechas.aDiaEpoch("2026-09-23")))
        assertEquals(0L, Fechas.aDiaEpoch("1970-01-01"))
    }

    @Test fun suma_dias_cruzando_meses_y_anio_bisiesto() {
        assertEquals("2026-09-30", Fechas.sumarDias("2026-09-23", 7))
        assertEquals("2026-10-02", Fechas.sumarDias("2026-09-25", 7))
        assertEquals("2028-03-01", Fechas.sumarDias("2028-02-28", 2)) // 2028 es bisiesto
        assertEquals("2026-12-31", Fechas.sumarDias("2027-01-01", -1))
    }

    @Test fun dias_entre_fechas() {
        assertEquals(5, Fechas.diasEntre("2026-09-16", "2026-09-21"))
        assertEquals(-18, Fechas.diasEntre("2026-09-22", "2026-09-04"))
    }
}
