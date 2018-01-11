package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.dialog_uploading.*

/**
 * Created by Aidan Laing on 2018-01-11.
 *
 */
class UploadingDialog(
        context: Context?,
        private val maxUploadCount: Int
) : Dialog(context) {

    private var currentUploadedCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_uploading)
        setCancelable(false)
        updateProgressText()
    }

    fun incrementUploadedCount() {
        currentUploadedCount++
        updateProgressText()
    }

    private fun updateProgressText() {
        val progressText = "$currentUploadedCount of $maxUploadCount"
        progressTv.text = progressText
    }
}