package com.pocketimps.testbandlab.wavetrimmer.core.session

import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveData
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveLoadException
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveLoader
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveSaveException
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveSaver
import java.io.InputStream
import java.io.OutputStream


class Session private constructor(val data: WaveData,
                                  val name: String) {
  @Throws(WaveSaveException::class)
  fun save(selection: IntRange, outputStream: OutputStream) {
    val saver = WaveSaver()
    saver.save(data.subList(selection.first, selection.last + 1), outputStream)
  }

  companion object {
    @Throws(WaveLoadException::class)
    fun createFromStream(name: String, inputStream: InputStream): Session {
      val loader = WaveLoader()
      val points = loader.load(inputStream)
      return Session(data = points,
                     name = name)
    }
  }
}
