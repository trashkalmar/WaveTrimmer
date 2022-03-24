package com.pocketimps.test.bandlab.wavetrimmer.app

import android.app.Application
import com.pocketimps.test.bandlab.wavetrimmer.app.di.dataModule
import com.pocketimps.test.bandlab.wavetrimmer.app.di.usecaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.dsl.koinApplication


interface AppKoin : KoinComponent {
  override fun getKoin() = KoinHolder.app!!.koin
}


private object KoinHolder {
  var app: KoinApplication? = null
}


class App : Application(), AppKoin {
  override fun onCreate() {
    super.onCreate()

    KoinHolder.app = koinApplication {
      androidContext(this@App)
      
      modules(dataModule,
              usecaseModule)
    }
  }
}
