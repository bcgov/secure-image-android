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

        fun addCloseListener()

        fun setToolbarTitle(title: String)

        fun setUpImagesList()
        fun addImagesListScrollChangeListener()
        fun showImages(items: ArrayList<Any>)
        fun scrollImagesListTo(position: Int)

        fun setViewedProgress(progress: Int)
    }

    interface Presenter : BasePresenter {
        fun closeClicked()

        fun imagesListScrollChanged(currentIndex: Int, imagesSize: Int, currentProgress: Int)
    }

}