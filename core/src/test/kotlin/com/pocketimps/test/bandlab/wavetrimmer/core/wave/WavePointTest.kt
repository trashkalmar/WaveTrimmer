package com.pocketimps.test.bandlab.wavetrimmer.core.wave

import com.pocketimps.testbandlab.wavetrimmer.core.ViewPort
import com.pocketimps.testbandlab.wavetrimmer.core.wave.BadWavePointException
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WavePoint
import org.junit.Assert
import org.junit.Test
import java.io.StringWriter


class WavePointTest {
  private fun String.toWavePoint(failTest: Boolean = true) = try {
    WavePoint.create(this)
  } catch (e: BadWavePointException) {
    if (failTest)
      Assert.fail(e.message)
    null
  }
  

  @Test
  fun `Parse single line to wave point`() {
    val line = "-0.1133728    0.113464355"
    val point = line.toWavePoint() ?: return

    Assert.assertEquals(-0.1133728F, point.low)
    Assert.assertEquals(0.113464355F, point.high)
  }

  @Test
  fun `Fix wave point`() {
    // Low and high values are swapped
    var line = "0.1234    -0.1234"
    var point = line.toWavePoint() ?: return

    Assert.assertEquals(-0.1234F, point.low)
    Assert.assertEquals(0.1234F, point.high)

    // Values are out of [-1, 1] range
    line = "-99.0000    99.0000"
    point = line.toWavePoint() ?: return
    Assert.assertEquals(-1.0F, point.low)
    Assert.assertEquals(1.0F, point.high)
  }

  @Test
  fun `Failed to parse wave point`() {
    // Single component
    var line = "0.1"
    line.toWavePoint(failTest = false)?.run {
      Assert.fail("Single component: test should not succeed")
      return
    }

    // Not a number
    line = "-0.1234 ABC"
    line.toWavePoint(failTest = false)?.run {
      Assert.fail("Not a number: test should not succeed")
      return
    }
  }

  @Test
  fun `Write to string`() {
    val point = WavePoint.create(-0.123F, 0.555F)

    val text = StringWriter().also(point::writeTo).toString()
    Assert.assertEquals("-0.123 0.555\n", text)
  }

  @Test
  fun `Scale wave point to view port`() {
    val vp = ViewPort(100, 100)

    var p = WavePoint.create(-1.0F, 1.0F)
    var scaled = p.scaleToViewPort(vp)
    Assert.assertEquals(100.0F, scaled.low)
    Assert.assertEquals(0.0F, scaled.high)

    p = WavePoint.create(-0.5F, 0.0F)
    scaled = p.scaleToViewPort(vp)
    Assert.assertEquals(75.0F, scaled.low)
    Assert.assertEquals(50.0F, scaled.high)

    p = WavePoint.create(0.25F, 0.9F)
    scaled = p.scaleToViewPort(vp)
    Assert.assertEquals(37.5F, scaled.low)
    Assert.assertEquals(5.0F, scaled.high, 0.001F)
  }
}
