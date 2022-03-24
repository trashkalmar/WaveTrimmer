package com.pocketimps.test.bandlab.wavetrimmer.core.wave

import com.pocketimps.testbandlab.wavetrimmer.core.wave.WavePoint
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveSaver
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.StringWriter


class WaveSaverTest {
  private val data = listOf(
    WavePoint.create(-1.0F, 0.5F),
    WavePoint.create(-0.5F, 0.0F),
    WavePoint.create(1.0F, -1.0F)
  )

  private val expected = "-1.0 0.5\n-0.5 0.0\n-1.0 1.0\n"

  @Test
  fun `Write wave data lines`() {
    val saver = WaveSaver()
    val text = StringWriter().also {
      saver.writeLines(data, it)
    }.toString()

    Assert.assertEquals(expected, text)
  }

  @Test
  fun `Write wave data to file`() {
    val saver = WaveSaver()

    val outFile = File(File(javaClass.getResource("/")!!.toURI()), "tmp.txt")
    outFile.outputStream().use {
      saver.save(data, it)
    }

    // Read the file
    val text = outFile.readText()
    outFile.delete()

    Assert.assertEquals(expected, text)
  }
}
