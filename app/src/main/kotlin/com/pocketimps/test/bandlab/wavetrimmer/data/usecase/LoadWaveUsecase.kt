package com.pocketimps.test.bandlab.wavetrimmer.data.usecase

import android.content.Context
import android.net.Uri
import com.pocketimps.extlib.tryOrNull
import com.pocketimps.test.bandlab.wavetrimmer.app.AppKoin
import com.pocketimps.testbandlab.wavetrimmer.core.session.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.inject


class LoadWaveUsecase(private val uri: Uri) : AppKoin {
  private val context: Context by inject()

  suspend fun execute() = withContext(Dispatchers.IO) {
    tryOrNull {
      val name = uri.lastPathSegment?.substringAfterLast('/').orEmpty()

      context.contentResolver.openInputStream(uri)?.use { inputStream ->
        Session.createFromStream(name, inputStream)
      }
    }
  }
}
