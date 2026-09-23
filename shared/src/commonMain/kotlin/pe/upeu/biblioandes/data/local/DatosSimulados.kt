package pe.upeu.biblioandes.data.local

import pe.upeu.biblioandes.domain.model.EstadoPrestamo
import pe.upeu.biblioandes.domain.model.Estudiante
import pe.upeu.biblioandes.domain.model.Libro
import pe.upeu.biblioandes.domain.model.Prestamo
import pe.upeu.biblioandes.domain.util.Fechas

/**
 * Datos semilla en memoria (anexo del caso). No hay red ni base de datos.
 */
object DatosSimulados {

    val estudiante = Estudiante(
        "E-2291", "Diego Huamán Ccama",
        "Ingeniería de Sistemas", "diego.huaman@correo.pe"
    )

    val categorias = listOf("Programación", "Matemática", "Redes", "Gestión", "Literatura")

    val libros = listOf(
        // --- Entregados en el anexo ---
        Libro(1, "Kotlin en profundidad", "M. Salazar", 2023, "Programación", "Central", 3),
        Libro(2, "Estructuras de datos", "R. Peña", 2021, "Programación", "Central", 0),
        Libro(3, "Cálculo aplicado", "L. Ortega", 2019, "Matemática", "Sede Norte", 2),
        Libro(4, "Redes de computadoras", "A. Medina", 2022, "Redes", "Sede Sur", 4),
        Libro(5, "Seguridad en redes", "P. Ríos", 2024, "Redes", "Central", 0),
        Libro(6, "Gestión de proyectos", "S. Delgado", 2021, "Gestión", "Sede Norte", 2),
        // --- Completados hasta doce ---
        Libro(7, "Programación en Python", "J. Quispe", 2024, "Programación", "Sede Norte", 5),
        Libro(8, "Álgebra lineal", "C. Vargas", 2020, "Matemática", "Sede Sur", 1),
        Libro(9, "Liderazgo y equipos ágiles", "V. Rojas", 2023, "Gestión", "Central", 3),
        Libro(10, "Cien años de soledad", "G. García Márquez", 1967, "Literatura", "Central", 2),
        Libro(11, "Los ríos profundos", "J. M. Arguedas", 1958, "Literatura", "Sede Sur", 0),
        Libro(12, "La ciudad y los perros", "M. Vargas Llosa", 1963, "Literatura", "Sede Norte", 1)
    )

    /**
     * Los cinco préstamos del anexo (2 Activos, 2 Devueltos, 1 Vencido).
     * Las fechas se calculan RELATIVAS a [hoy] para que los Activos siempre
     * queden en el futuro el día de la evaluación, como pide el anexo.
     */
    fun prestamosIniciales(hoy: String): List<Prestamo> {
        fun dia(offset: Int) = Fechas.sumarDias(hoy, offset)
        return listOf(
            Prestamo(1, libros[0], dia(-2), dia(5), EstadoPrestamo.Activo(5)),
            Prestamo(2, libros[3], dia(-1), dia(6), EstadoPrestamo.Activo(6)),
            Prestamo(3, libros[2], dia(-34), dia(-27), EstadoPrestamo.Devuelto(dia(-28))),
            Prestamo(4, libros[1], dia(-49), dia(-42), EstadoPrestamo.Devuelto(dia(-43))),
            Prestamo(5, libros[5], dia(-25), dia(-18), EstadoPrestamo.Vencido(18))
        )
    }
}
