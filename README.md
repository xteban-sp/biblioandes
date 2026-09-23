# BiblioAndes — Préstamos de biblioteca (KMP + Compose Multiplatform)

Examen Parcial U1 · Versión B · Desarrollo de Aplicaciones Móviles · UPeU 2026-2

App para que el estudiante consulte el catálogo, solicite préstamos y controle sus fechas de devolución.
Funciona **solo con datos simulados en memoria** (sin Ktor, Room, SQLDelight ni Retrofit), organizada para que
pasar a la API real sea **reemplazar una clase**, no reescribir la app.

## Stack

| Tema | Uso |
|---|---|
| Kotlin Multiplatform | Targets Android + iOS (`iosArm64`, `iosSimulatorArm64`) desde el módulo `:shared` |
| Compose Multiplatform + Material 3 | Toda la interfaz en `commonMain`, paleta propia, modo claro/oscuro |
| Navigation Compose (KMP) | Rutas tipadas `@Serializable`, barra inferior con 3 destinos |
| ViewModel KMP + StateFlow | Un `UiState` por pantalla, expuesto como `StateFlow` de solo lectura |
| Koin | Módulos `dataModule`, `domainModule`, `presentationModule` en `commonMain`; inicio en `MainApplication` (Android) y `KoinInit.kt` (iOS) |
| Corrutinas | `delay(800)` en el repositorio simulado; nada bloquea el hilo principal |

## Estructura de paquetes

```
shared/src/commonMain/kotlin/pe/upeu/biblioandes/
├── App.kt                      raíz de la UI (Android e iOS) — aquí vive el modo oscuro
├── domain/                     Kotlin puro, sin Compose
│   ├── model/                  Libro, Prestamo, EstadoPrestamo (sealed), Estudiante,
│   │                           ReglasPrestamo (RN-01..04), FiltroEstado
│   ├── repository/             BibliotecaRepository (solo la interfaz)
│   ├── usecase/                ObtenerCatalogo, SolicitarPrestamo, ObtenerPrestamos,
│   │                           ObtenerLibro, ObtenerEstudiante, ObtenerResumenInicio, DevolverPrestamo
│   └── util/                   Fechas (ISO sin librerías), Reloj (interfaz), normalizar() de texto
├── data/
│   ├── local/                  DatosSimulados (12 libros, 5 préstamos), RelojSistema
│   └── repository/             BibliotecaRepositoryFake (memoria + 800 ms + bandera de error)
├── presentation/
│   ├── inicio/  catalogo/  detalle/  prestamos/  perfil/   Screen + ViewModel + UiState
│   ├── components/             composables reutilizables (estados, chips, tarjetas)
│   ├── navigation/             Destinos.kt (rutas tipadas), AppNavHost.kt
│   └── theme/                  Color.kt, Type.kt, BiblioAndesTheme.kt
└── di/                         AppModule.kt (Koin)
```

## Decisiones de arquitectura

1. **Las cuatro reglas viven en `domain/model/ReglasPrestamo.kt`.** Los casos de uso las llaman; las pantallas solo muestran el resultado.
   - RN-01 `MAX_PRESTAMOS_ACTIVOS = 3` · RN-02 `ejemplaresDisponibles <= 0` · RN-03 `DIAS_DE_PRESTAMO = 7` + `actualizarEstado()` · RN-04 `contarVencidos() > 0`.
2. **`EstadoPrestamo` es una sealed class**: cada estado lleva su propio dato (días restantes, fecha de devolución, días de atraso) y el `when` es exhaustivo.
3. **El dominio no conoce la implementación**: `BibliotecaRepository` es una interfaz; `BibliotecaRepositoryFake` la implementa y Koin la inyecta por su interfaz.
4. **La fecha de hoy se inyecta (`Reloj`)**: las reglas de fechas se prueban con un reloj fijo. Los datos semilla calculan sus fechas relativas a hoy, así los préstamos Activos siempre están en el futuro.
5. **State hoisting**: cada pantalla tiene una versión con ViewModel (`XxxScreen`) y otra sin estado (`XxxContenido`) que solo recibe datos y callbacks.
6. **Modo oscuro**: `rememberSaveable` en `App()`, en la cima del árbol; `PerfilScreen` solo recibe el valor y un callback.
7. **Devolución**: se agregó "Registrar devolución" en *Mis préstamos* para poder regularizar un Vencido (RN-04) y demostrar todo el flujo.

## Conectar la API real (Unidad 2)

1. Crear `data/remote/…` (cliente y DTOs) y `data/repository/BibliotecaRepositoryApi.kt` que implemente `BibliotecaRepository`.
2. En `di/AppModule.kt` cambiar **una línea**:
   `single<BibliotecaRepository> { BibliotecaRepositoryApi(get()) }`
3. Agregar las dependencias de red en `gradle/libs.versions.toml` y `shared/build.gradle.kts`.

La UI, los ViewModels, los casos de uso y las reglas **no se tocan**.

## Ejecutar

- **Android**: abrir la carpeta en Android Studio → configuración `androidApp` → Run (emulador o dispositivo).
- **iOS**: en una Mac, abrir `iosApp/iosApp.xcodeproj` en Xcode → simulador → Run.
- **Pruebas del dominio** (13 pruebas de reglas, fechas y casos de uso):
  `./gradlew :shared:testAndroidHostTest`

### Estado de error del catálogo

En `data/repository/BibliotecaRepositoryFake.kt` cambiar `SIMULAR_ERROR_CATALOGO = true` y volver a ejecutar.
El catálogo muestra "Algo salió mal" con el botón **Reintentar**.

## Requerimientos → dónde están

| Código | Pantalla / archivo |
|---|---|
| RF-01 Inicio | `presentation/inicio/InicioScreen.kt` + `ObtenerResumenInicioUseCase` |
| RF-02 Catálogo | `presentation/catalogo/*` + `ObtenerCatalogoUseCase.filtrar()` |
| RF-03 Detalle | `presentation/detalle/*` + `SolicitarPrestamoUseCase` (diálogo de confirmación) |
| RF-04 Mis préstamos | `presentation/prestamos/*` + `ObtenerPrestamosUseCase` (orden + filtro) |
| RF-05 Búsqueda | `domain/util/Texto.kt` `normalizar()` (sin mayúsculas ni tildes) |
| RF-06 Perfil y ajustes | `presentation/perfil/*` + `App.kt` |
| RF-07 Navegación | `presentation/navigation/AppNavHost.kt`, `Destinos.kt` |
