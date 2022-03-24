package com.pocketimps.test.bandlab.wavetrimmer.core.wave

import com.pocketimps.testbandlab.wavetrimmer.core.wave.BadWavePointException
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveLoader
import org.junit.Assert
import org.junit.Test
import java.io.File


class WaveLoaderTest {
  @Test
  fun `Parse lines to wave points`() {
    val lines = """
      -0.0070495605 0.008026123
      -0.053771973  0.058654785
      -0.26589966   0.3184204
    """.split("\n")

    val loader = WaveLoader()
    val points = try {
      loader.readLines(lines)
    } catch (e: BadWavePointException) {
      Assert.fail(e.message)
      return
    }

    Assert.assertEquals(-0.0070495605F, points[0].low)
    Assert.assertEquals(0.008026123F, points[0].high)
    Assert.assertEquals(-0.053771973F, points[1].low)
    Assert.assertEquals(0.058654785F, points[1].high)
    Assert.assertEquals(-0.26589966F, points[2].low)
    Assert.assertEquals(0.3184204F, points[2].high)
  }

  @Test
  fun `Load wave from file`() {
    val inputFile = File(javaClass.getResource("/sample.txt")!!.toURI())

    val loader = WaveLoader()
    val points = try {
      inputFile.inputStream().use(loader::load)
    } catch (e: BadWavePointException) {
      Assert.fail(e.message)
      return
    }

    Assert.assertFalse(points.isEmpty())
  }
}
