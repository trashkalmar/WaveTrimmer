package com.pocketimps.test.bandlab.wavetrimmer.data.usecase

import android.content.Context
import android.net.Uri
import com.pocketimps.test.bandlab.wavetrimmer.app.AppKoin
import com.pocketimps.testbandlab.wavetrimmer.core.session.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.inject


class ExportSelectionUsecase(private val session: Session,
                             private val selection: IntRange,
                             private val uri: Uri)
    : AppKoin {
  private val context: Context by inject()

  suspend fun execute() {
    withContext(Dispatchers.IO) {
      // BUGBUGBUG!! On Android 11, "w" mode is broken in ContentResolver.open*. Append mode is used always.
      // Workaround: use "wt" mode instead.
      context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
        session.save(selection, outputStream)
      }
    }
  }
}
