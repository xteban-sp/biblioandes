package pe.upeu.biblioandes.domain.repository

import kotlinx.coroutines.flow.Flow
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo

/**
 * CONTRATO del repositorio (solo la interfaz). El dominio y la interfaz de usuario
 * dependen de este contrato, nunca de una implementación concreta.
 *
 * Hoy lo implementa BibliotecaRepositoryFake (datos en memoria).
 * Cuando exista el servicio web se crea otra clase que implemente esta misma
 * interfaz y se cambia UNA línea en di/AppModule.kt.
 */
interface BibliotecaRepository {

    suspend fun obtenerEstudiante(): Estudiante

    suspend fun obtenerCategorias(): List<String>

    /** Catálogo completo; emite de nuevo cuando cambia el stock. */
    fun observarLibros(): Flow<List<Libro>>

    /** Préstamos del estudiante; emite de nuevo cuando se registra uno. */
    fun observarPrestamos(): Flow<List<Prestamo>>

    /** Libro por id, o null si no existe (por eso el tipo es anulable). */
    suspend fun obtenerLibro(id: Int): Libro?

    suspend fun obtenerPrestamos(): List<Prestamo>

    /** Registra el préstamo y descuenta un ejemplar. No valida reglas: eso es del dominio. */
    suspend fun registrarPrestamo(libroId: Int, fechaPrestamo: String, fechaLimite: String): Prestamo

    /** Marca el préstamo como Devuelto y repone el ejemplar (permite "regularizar", RN-04). */
    suspend fun registrarDevolucion(prestamoId: Int, fechaDevolucion: String)
}
