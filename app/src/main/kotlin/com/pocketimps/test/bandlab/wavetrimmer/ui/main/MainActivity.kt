package com.pocketimps.test.bandlab.wavetrimmer.ui.main

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.pocketimps.extlib.make
import com.pocketimps.test.bandlab.wavetrimmer.app.AppKoin
import com.pocketimps.test.bandlab.wavetrimmer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.coroutines.CoroutineContext


class MainActivity
    : AppCompatActivity(),
      AppKoin,
      CoroutineScope {
  private lateinit var binding: ActivityMainBinding
  private val vm by viewModel<MainViewModel>()

  private val rootJob = SupervisorJob()
  override val coroutineContext: CoroutineContext
    get() = rootJob + Dispatchers.Main.immediate

  private lateinit var filePickerLauncher: ActivityResultLauncher<Unit>
  private lateinit var fileSaverLauncher: ActivityResultLauncher<Unit>

  private var selection = 0..0

  private fun onWaveSelectionChanged(newSelection: IntRange) {
    selection = newSelection
    val hasData = (vm.session?.data?.isEmpty() == false)

    binding.exportSelected.isEnabled = hasData && (selection.last - selection.first > 1)
    binding.resetSelection.isEnabled = hasData
  }

  private fun registerFileManagers() {
    filePickerLauncher = registerForActivityResult(FilePickerRequestContract()) { uri ->
      uri?.make(vm::createSessionFromUri)
    }

    fileSaverLauncher = registerForActivityResult(FileSaverRequestContract()) { uri ->
      uri?.make {
        vm.exportSelection(selection, it)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    registerFileManagers()

    binding.apply {
      setContentView(binding.root)

      openWave.setOnClickListener {
        filePickerLauncher.launch(Unit)
      }

      exportSelected.setOnClickListener {
        fileSaverLauncher.launch(Unit)
      }

      resetSelection.setOnClickListener {
        waveView.resetSelection()
      }

      launch {
        // Observe WaveView state changes
        waveView.stateFlow.collect(::onWaveSelectionChanged)
      }

      launch {
        // Observe session changes
        vm.stateFlow.collect { session ->
          waveName.text = session?.name
          waveView.setData(session?.data ?: emptyList())
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    rootJob.cancel()
  }
}
