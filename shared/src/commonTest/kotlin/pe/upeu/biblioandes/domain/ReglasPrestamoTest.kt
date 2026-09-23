package pe.upeu.biblioandes.domain

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.MotivoRechazo
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ReglasPrestamoTest {

    private val libro = Libro(1, "Kotlin", "Autor", 2023, "Programación", "Central", 3)
    private val agotado = libro.copy(id = 2, ejemplaresDisponibles = 0)

    private fun prestamo(id: Int, estado: EstadoPrestamo, limite: String = "2026-09-30") =
        Prestamo(id, libro, "2026-09-23", limite, estado)

    @Test fun rn01_no_mas_de_tres_activos() {
        val tres = (1..3).map { prestamo(it, EstadoPrestamo.Activo(5)) }
        assertIs<MotivoRechazo.LimiteDeActivos>(ReglasPrestamo.motivoDeRechazo(libro, tres))
        assertNull(ReglasPrestamo.motivoDeRechazo(libro, tres.take(2)))
    }

    @Test fun rn02_no_se_pide_un_libro_sin_ejemplares() {
        assertEquals(MotivoRechazo.SinEjemplares, ReglasPrestamo.motivoDeRechazo(agotado, emptyList()))
    }

    @Test fun rn03_dura_siete_dias_y_pasa_a_vencido() {
        assertEquals("2026-09-30", ReglasPrestamo.calcularFechaLimite("2026-09-23"))
        val activo = prestamo(1, EstadoPrestamo.Activo(7), limite = "2026-09-20")
        assertEquals(EstadoPrestamo.Vencido(3), ReglasPrestamo.actualizarEstado(activo, hoy = "2026-09-23").estado)
        val vigente = prestamo(2, EstadoPrestamo.Activo(0), limite = "2026-09-25")
        assertEquals(EstadoPrestamo.Activo(2), ReglasPrestamo.actualizarEstado(vigente, hoy = "2026-09-23").estado)
    }

    @Test fun rn03_un_devuelto_no_cambia() {
        val devuelto = prestamo(1, EstadoPrestamo.Devuelto("2026-09-01"), limite = "2026-08-01")
        assertEquals(devuelto, ReglasPrestamo.actualizarEstado(devuelto, hoy = "2026-09-23"))
    }

    @Test fun rn04_con_un_vencido_no_puede_pedir() {
        val lista = listOf(prestamo(1, EstadoPrestamo.Vencido(2)))
        assertIs<MotivoRechazo.TienePrestamoVencido>(ReglasPrestamo.motivoDeRechazo(libro, lista))
    }
}
