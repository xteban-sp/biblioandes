package pe.upeu.biblioandes.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pe.upeu.biblioandes.data.local.DatosSimulados
import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.model.ReglasPrestamo
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.util.Reloj

/**
 * Implementación SIMULADA del repositorio: datos en memoria + retardo de 800 ms.
 * Es la ÚNICA clase que se reemplaza cuando exista el servicio web.
 *
 * @param simularErrorCatalogo bandera para mostrar el estado de error del catálogo.
 *        Para demostrarlo: cambiar SIMULAR_ERROR_CATALOGO a true y volver a ejecutar.
 */
class BibliotecaRepositoryFake(
    reloj: Reloj,
    private val simularErrorCatalogo: Boolean = SIMULAR_ERROR_CATALOGO
) : BibliotecaRepository {

    // MutableStateFlow: al cambiar el stock o los préstamos, quien observa se entera solo.
    private val libros = MutableStateFlow(DatosSimulados.libros)
    private val prestamos = MutableStateFlow(DatosSimulados.prestamosIniciales(reloj.hoy()))
    private val mutex = Mutex()

    override suspend fun obtenerEstudiante(): Estudiante {
        delay(RETARDO_MS)
        return DatosSimulados.estudiante
    }

    override suspend fun obtenerCategorias(): List<String> = DatosSimulados.categorias

    override fun observarLibros(): Flow<List<Libro>> = flow {
        delay(RETARDO_MS) // simula la latencia de red; no bloquea el hilo principal
        if (simularErrorCatalogo) throw IllegalStateException("No se pudo conectar con el catálogo")
        emitAll(libros)
    }

    override fun observarPrestamos(): Flow<List<Prestamo>> = flow {
        delay(RETARDO_MS)
        emitAll(prestamos)
    }

    override suspend fun obtenerLibro(id: Int): Libro? = libros.value.firstOrNull { it.id == id }

    override suspend fun obtenerPrestamos(): List<Prestamo> {
        delay(RETARDO_MS)
        return prestamos.value
    }

    override suspend fun registrarPrestamo(
        libroId: Int,
        fechaPrestamo: String,
        fechaLimite: String
    ): Prestamo = mutex.withLock {
        val libro = requireNotNull(libros.value.firstOrNull { it.id == libroId }) { "Libro $libroId no existe" }
        libros.update { lista ->
            lista.map { if (it.id == libroId) it.copy(ejemplaresDisponibles = it.ejemplaresDisponibles - 1) else it }
        }
        val nuevo = Prestamo(
            id = (prestamos.value.maxOfOrNull { it.id } ?: 0) + 1,
            libro = libro,
            fechaPrestamo = fechaPrestamo,
            fechaLimite = fechaLimite,
            estado = EstadoPrestamo.Activo(ReglasPrestamo.DIAS_DE_PRESTAMO)
        )
        prestamos.update { it + nuevo }
        nuevo
    }

    override suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String) {
        mutex.withLock {
            val prestamo = prestamos.value.firstOrNull { it.id == prestamoId } ?: return
            if (prestamo.estado is EstadoPrestamo.Devuelto) return
            prestamos.update { lista ->
                lista.map { if (it.id == prestamoId) it.copy(estado = EstadoPrestamo.Devuelto(fechaDevolucion)) else it }
            }
            libros.update { lista ->
                lista.map {
                    if (it.id == prestamo.libro.id) it.copy(ejemplaresDisponibles = it.ejemplaresDisponibles + 1) else it
                }
            }
        }
    }

    companion object {
        const val RETARDO_MS = 800L
        /** Cambiar a true para ver el estado de error en el catálogo. */
        const val SIMULAR_ERROR_CATALOGO = false
    }
}
