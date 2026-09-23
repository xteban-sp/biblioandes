package pe.upeu.biblioandes.domain.model

/** Estudiante que usa la aplicación (uno fijo, según el anexo del caso). */
data class Estudiante(
    val codigo: String,
    val nombre: String,
    val carrera: String,
    val correo: String
)
