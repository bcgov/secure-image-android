package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface CreateAlbumContract {

    interface View: BaseView<Presenter> {
        fun finish()
        fun setUpBackListener()

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)

        fun setUpViewAllImagesListener()
        fun showViewAllImages()
        fun hideViewAllImages()
        fun goToAllImages()

        fun setUpUploadListener()

        fun goToSecureCamera()
    }

    interface Presenter: BasePresenter {
        fun backClicked()

        fun uploadClicked()

        fun viewAllImagesClicked()

        fun addImagesClicked()
    }

}