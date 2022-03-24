package com.pocketimps.test.bandlab.wavetrimmer.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract


class FilePickerRequestContract : ActivityResultContract<Unit, Uri?>() {
  override fun createIntent(context: Context, input: Unit) =
    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
      type = "text/plain"
      addCategory(Intent.CATEGORY_OPENABLE)
    }

  override fun parseResult(resultCode: Int, intent: Intent?) =
    intent?.data?.takeIf {
      resultCode == Activity.RESULT_OK
    }
}
