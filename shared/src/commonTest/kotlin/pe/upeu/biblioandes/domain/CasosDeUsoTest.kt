package pe.upeu.biblioandes.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.MotivoRechazo
import pe.upeu.biblioandes.domain.model.ResultadoSolicitud
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.domain.util.Reloj
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CasosDeUsoTest {

    private val reloj = Reloj { "2026-09-23" } // reloj fijo: pruebas repetibles
    private val repo = BibliotecaRepositoryFake(reloj)
    private val catalogo = ObtenerCatalogoUseCase(repo)
    private val prestamos = ObtenerPrestamosUseCase(repo, reloj)
    private val solicitar = SolicitarPrestamoUseCase(repo, prestamos, reloj)
    private val devolver = DevolverPrestamoUseCase(repo, reloj)

    @Test fun datos_semilla_cumplen_el_anexo() = runTest {
        assertEquals(12, DatosSimulados.libros.size)
        assertEquals(5, DatosSimulados.categorias.size)
        assertTrue(DatosSimulados.libros.count { it.ejemplaresDisponibles == 0 } >= 2)
        val lista = prestamos.actuales()
        assertEquals(2, lista.count { it.estado is EstadoPrestamo.Activo })
        assertEquals(2, lista.count { it.estado is EstadoPrestamo.Devuelto })
        assertEquals(1, lista.count { it.estado is EstadoPrestamo.Vencido })
    }

    @Test fun busqueda_sin_mayusculas_ni_tildes() = runTest {
        val libros = catalogo().first()
        assertEquals(listOf("Cálculo aplicado"), catalogo.filtrar(libros, null, "CALCULO").map { it.titulo })
        assertEquals(listOf("Los ríos profundos"), catalogo.filtrar(libros, null, "arguedas").map { it.titulo })
        assertEquals(3, catalogo.filtrar(libros, "Literatura", "").size)
        assertEquals(1, catalogo.filtrar(libros, "Redes", "rios").size)
    }

    @Test fun prestamos_ordenados_por_fecha_de_devolucion() = runTest {
        val lista = prestamos().first()
        val pendientes = lista.filter { it.estado !is EstadoPrestamo.Devuelto }
        assertEquals(pendientes.sortedBy { it.fechaLimite }, pendientes)
        assertIs<EstadoPrestamo.Vencido>(lista.first().estado)
    }

    @Test fun flujo_completo_de_reglas() = runTest {
        // Con la semilla hay un Vencido: RN-04 bloquea.
        val r1 = solicitar(7)
        assertIs<ResultadoSolicitud.Rechazada>(r1)
        assertIs<MotivoRechazo.TienePrestamoVencido>(r1.motivo)

        // Se regulariza el vencido (préstamo 5) y ya se puede pedir.
        devolver(5)
        val r2 = solicitar(7)
        assertIs<ResultadoSolicitud.Aprobada>(r2)
        assertEquals("2026-09-30", r2.prestamo.fechaLimite)          // RN-03: 7 días
        assertEquals(4, repo.obtenerLibro(7)!!.ejemplaresDisponibles) // stock descontado

        // Ya hay 3 activos: RN-01 bloquea.
        val r3 = solicitar(9)
        assertIs<ResultadoSolicitud.Rechazada>(r3)
        assertIs<MotivoRechazo.LimiteDeActivos>(r3.motivo)
    }

    @Test fun rn02_libro_sin_ejemplares() = runTest {
        devolver(5)
        val r = solicitar(2) // "Estructuras de datos" tiene 0 ejemplares
        assertIs<ResultadoSolicitud.Rechazada>(r)
        assertEquals(MotivoRechazo.SinEjemplares, r.motivo)
    }
}
