package com.pocketimps.test.bandlab.wavetrimmer.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketimps.test.bandlab.wavetrimmer.app.AppKoin
import com.pocketimps.test.bandlab.wavetrimmer.data.usecase.ExportSelectionUsecase
import com.pocketimps.test.bandlab.wavetrimmer.data.usecase.LoadWaveUsecase
import com.pocketimps.testbandlab.wavetrimmer.core.session.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf


class MainViewModel
    : ViewModel(),
      AppKoin {
  var session: Session? = null
    private set

  private val _stateFlow = MutableStateFlow(session)
  val stateFlow: Flow<Session?> = _stateFlow

  fun createSessionFromUri(uri: Uri) {
    viewModelScope.launch {
      val uc = get<LoadWaveUsecase> {
        parametersOf(uri)
      }
      session = uc.execute().also {
        _stateFlow.emit(it)
      }
    }
  }

  fun exportSelection(selection: IntRange, uri: Uri) {
    viewModelScope.launch {
      val uc = get<ExportSelectionUsecase> {
        parametersOf(session, selection, uri)
      }
      uc.execute()
    }
  }
}
