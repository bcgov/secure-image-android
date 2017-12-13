package ca.bc.gov.secureimage.screens.allimages

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
interface AllImagesContract {

    interface View: BaseView<Presenter> {
        fun finish()

        fun setUpBackListener()

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)

        fun goToSecureCamera()
    }

    interface Presenter: BasePresenter {
        fun backClicked()

        fun addImagesClicked()
    }

}