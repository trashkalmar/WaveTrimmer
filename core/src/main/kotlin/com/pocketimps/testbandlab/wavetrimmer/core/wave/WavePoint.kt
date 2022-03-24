package com.pocketimps.testbandlab.wavetrimmer.core.wave

import com.pocketimps.testbandlab.wavetrimmer.core.ViewPort
import java.io.Writer
import java.lang.Float.max
import java.lang.Float.min


typealias WaveData = List<WavePoint>


class WavePoint private constructor(val low: Float,
                                    val high: Float) {
  override fun toString() = "[$low, $high]"

  fun scaleToViewPort(viewPort: ViewPort): WavePoint {
    val dy = viewPort.height.toFloat() / 2.0F
    val low = (1.0F - low) * dy
    val high = (1.0F - high) * dy

    return WavePoint(low, high)
  }

  fun writeTo(writer: Writer) {
    writer.write("$low $high\n")
  }


  companion object {
    fun create(low: Float, high: Float): WavePoint {
      val realLow = min(high, low).coerceIn(-1.0F, 1.0F)
      val realHigh = max(high, low).coerceIn(-1.0F, 1.0F)
      return WavePoint(low = realLow,
                       high = realHigh)
    }

    @Throws(BadWavePointException::class)
    fun create(line: String): WavePoint {
      val parts = line.split(' ')
      if (parts.size < 2)
        throw BadWavePointException(line)

      val first = parts.first()
      val last = parts.last()

      return try {
        val low = first.toFloat()
        val high = last.toFloat()
        create(low, high)
      } catch (e: Exception) {
        throw BadWavePointException(line, e)
      }
    }
  }
}
