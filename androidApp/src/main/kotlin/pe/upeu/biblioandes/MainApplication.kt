package pe.upeu.biblioandes

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import pe.upeu.biblioandes.di.initKoin

/** Inicializa Koin una sola vez, al arrancar el proceso de la app en Android. */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MainApplication)
        }
    }
}
