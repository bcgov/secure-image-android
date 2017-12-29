package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView
import ca.bc.gov.secureimage.data.models.CameraImage

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface CreateAlbumContract {

    interface View: BaseView<Presenter> {
        fun finish()

        fun setRefresh(refresh: Boolean)

        fun showError(message: String)
        fun showMessage(message: String)

        fun setUpBackListener()

        fun showNetworkType()
        fun hideNetworkType()
        fun setNetworkTypeText(text: String)

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)

        fun setUpViewAllImagesListener()
        fun showViewAllImages()
        fun hideViewAllImages()
        fun goToAllImages(albumKey: String)

        fun setUpSaveListener()

        fun setUpDeleteListener()
        fun showDeleteAlbumDialog()
        fun hideDeleteAlbumDialog()

        fun setAlbumName(albumName: String)

        fun goToSecureCamera(albumKey: String)
        fun goToImageDetail(imageKey: String)
    }

    interface Presenter: BasePresenter {
        fun viewShown(refresh: Boolean)
        fun viewHidden()

        fun backClicked()

        fun saveClicked(albumName: String)

        fun deleteClicked()
        fun deleteForeverClicked()

        fun viewAllImagesClicked()

        fun addImagesClicked()
        fun imageClicked(cameraImage: CameraImage)
    }

}