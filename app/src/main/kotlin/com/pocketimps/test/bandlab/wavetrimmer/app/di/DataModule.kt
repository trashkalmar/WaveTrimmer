package com.pocketimps.test.bandlab.wavetrimmer.app.di

import com.pocketimps.test.bandlab.wavetrimmer.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {
  viewModel {
    MainViewModel()
  }
}
