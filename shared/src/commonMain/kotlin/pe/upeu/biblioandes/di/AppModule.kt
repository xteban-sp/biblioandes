package pe.upeu.biblioandes.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import pe.upeu.biblioandes.data.local.RelojSistema
import pe.upeu.biblioandes.data.repository.BibliotecaRepositoryFake
import pe.upeu.biblioandes.domain.repository.BibliotecaRepository
import pe.upeu.biblioandes.domain.usecase.DevolverPrestamoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerCatalogoUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerEstudianteUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerLibroUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerPrestamosUseCase
import pe.upeu.biblioandes.domain.usecase.ObtenerResumenInicioUseCase
import pe.upeu.biblioandes.domain.usecase.SolicitarPrestamoUseCase
import pe.upeu.biblioandes.domain.util.Reloj
import pe.upeu.biblioandes.presentation.catalogo.CatalogoViewModel
import pe.upeu.biblioandes.presentation.detalle.DetalleLibroViewModel
import pe.upeu.biblioandes.presentation.inicio.InicioViewModel
import pe.upeu.biblioandes.presentation.perfil.PerfilViewModel
import pe.upeu.biblioandes.presentation.prestamos.PrestamosViewModel

/**
 * ÚNICO lugar donde se nombran las implementaciones concretas.
 *
 * El repositorio se registra por su INTERFAZ y como `single` (una sola instancia),
 * porque los datos viven en memoria: si hubiera dos, cada pantalla vería datos distintos.
 * El día que exista el servicio web, se cambia solo esta línea:
 *     single<BibliotecaRepository> { BibliotecaRepositoryApi(get()) }
 */
val dataModule = module {
    single<Reloj> { RelojSistema() }
    single<BibliotecaRepository> { BibliotecaRepositoryFake(get()) }
}

/** Casos de uso: sin estado, así que `factory` (instancia nueva cada vez). */
val domainModule = module {
    factoryOf(::ObtenerCatalogoUseCase)
    factoryOf(::ObtenerLibroUseCase)
    factoryOf(::ObtenerPrestamosUseCase)
    factoryOf(::ObtenerEstudianteUseCase)
    factoryOf(::ObtenerResumenInicioUseCase)
    factoryOf(::SolicitarPrestamoUseCase)
    factoryOf(::DevolverPrestamoUseCase)
}

/** ViewModels: su ciclo de vida lo maneja Koin junto con la navegación. */
val presentationModule = module {
    viewModelOf(::InicioViewModel)
    viewModelOf(::CatalogoViewModel)
    viewModelOf(::PrestamosViewModel)
    viewModelOf(::PerfilViewModel)
    // El detalle recibe el id del libro como parámetro al crearse: koinViewModel { parametersOf(id) }
    viewModel { (libroId: Int) -> DetalleLibroViewModel(libroId, get(), get(), get()) }
}

/** Cada plataforma puede aportar sus propias dependencias (Android / iOS). */
expect val platformModule: Module

fun initKoin(config: (KoinApplication.() -> Unit)? = null) = startKoin {
    config?.invoke(this)
    modules(dataModule, domainModule, presentationModule, platformModule)
}
