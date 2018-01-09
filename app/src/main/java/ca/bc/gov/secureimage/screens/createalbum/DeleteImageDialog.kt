package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.local.CameraImage
import kotlinx.android.synthetic.main.dialog_delete_image.*

/**
 * Created by Aidan Laing on 2018-01-08.
 *
 */
class DeleteImageDialog(
        context: Context?,
        private val deleteListener: DeleteListener,
        private val cameraImage: CameraImage,
        private val position: Int
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_image)

        cancelTv.setOnClickListener {
            hide()
        }

        deleteTv.setOnClickListener {
            deleteListener.deleteImageConfirmed(cameraImage, position)
            hide()
        }
    }

    interface DeleteListener {
        fun deleteImageConfirmed(cameraImage: CameraImage, position: Int)
    }
}