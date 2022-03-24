package com.pocketimps.test.bandlab.wavetrimmer.app.di

import com.pocketimps.test.bandlab.wavetrimmer.data.usecase.ExportSelectionUsecase
import com.pocketimps.test.bandlab.wavetrimmer.data.usecase.LoadWaveUsecase
import org.koin.dsl.module


val usecaseModule = module {
  factory { args ->
    LoadWaveUsecase(args.get())
  }

  factory { args ->
    ExportSelectionUsecase(args.get(), args.get(), args.get())
  }
}
