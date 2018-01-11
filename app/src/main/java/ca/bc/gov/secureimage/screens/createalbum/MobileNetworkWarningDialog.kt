package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.dialog_mobile_network_warning.*

/**
 * Created by Aidan Laing on 2018-01-11.
 *
 */
class MobileNetworkWarningDialog(
        context: Context?,
        private val uploadListener: UploadListener
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_mobile_network_warning)

        cancelTv.setOnClickListener {
            hide()
        }

        uploadAnywayTv.setOnClickListener {
            uploadListener.uploadAnywayClicked()
            hide()
        }
    }

    interface UploadListener {
        fun uploadAnywayClicked()
    }
}