package ca.bc.gov.secureimage.screens.allimages

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView
import ca.bc.gov.secureimage.data.models.CameraImage

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
interface AllImagesContract {

    interface View: BaseView<Presenter> {
        fun finish()

        fun showError(message: String)

        fun setUpBackListener()

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)

        fun goToSecureCamera(albumKey: String)
        fun goToImageDetail(imageKey: String)
    }

    interface Presenter: BasePresenter {
        fun viewShown()
        fun viewHidden()

        fun backClicked()

        fun addImagesClicked()
        fun imageClicked(cameraImage: CameraImage)
    }

}