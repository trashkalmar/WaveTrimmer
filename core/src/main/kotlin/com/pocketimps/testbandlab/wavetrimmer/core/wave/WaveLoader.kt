package com.pocketimps.testbandlab.wavetrimmer.core.wave

import java.io.InputStream


class WaveLoader {
  @Throws(BadWavePointException::class)
  fun readLines(lines: List<String>) =
    lines.map(String::trim)
         .filter(String::isNotEmpty)
         .map(WavePoint.Companion::create)

  @Throws(WaveLoadException::class)
  fun load(inputStream: InputStream) = try {
    val lines = inputStream.bufferedReader().use {
      it.readLines()
    }
    
    readLines(lines)
  } catch (e: Exception) {
    throw WaveLoadException(e)
  }
}
