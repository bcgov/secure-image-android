package ca.bc.gov.secureimage.screens.allimages

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.local.CameraImage
import kotlinx.android.synthetic.main.dialog_delete_images.*

/**
 * Created by Aidan Laing on 2018-01-04.
 *
 */
class DeleteImagesDialog(
        context: Context?,
        private val deleteListener: DeleteListener,
        private val cameraImages: ArrayList<CameraImage>
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_images)

        cancelTv.setOnClickListener {
            hide()
        }

        deleteTv.setOnClickListener {
            deleteListener.deleteConfirmed(cameraImages)
            hide()
        }
    }

    interface DeleteListener {
        fun deleteConfirmed(cameraImages: ArrayList<CameraImage>)
    }
}