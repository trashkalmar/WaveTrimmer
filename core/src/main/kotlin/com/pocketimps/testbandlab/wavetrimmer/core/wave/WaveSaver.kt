package com.pocketimps.testbandlab.wavetrimmer.core.wave

import java.io.OutputStream
import java.io.Writer


class WaveSaver {
  fun writeLines(data: WaveData, writer: Writer) {
    data.forEach {
      it.writeTo(writer)
    }
  }

  @Throws(WaveSaveException::class)
  fun save(data: WaveData, outputStream: OutputStream) = try {
    outputStream.bufferedWriter().use {
      writeLines(data, it)
    }
  } catch (e: Exception) {
    throw WaveSaveException(e)
  }
}
