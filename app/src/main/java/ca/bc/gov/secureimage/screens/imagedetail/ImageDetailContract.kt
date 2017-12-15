package ca.bc.gov.secureimage.screens.imagedetail

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
interface ImageDetailContract {

    interface View : BaseView<Presenter> {
        fun finish()

        fun showError(message: String)

        fun addBackListener()

        fun showImage(byteArray: ByteArray)
    }

    interface Presenter : BasePresenter {
        fun backClicked()
    }

}