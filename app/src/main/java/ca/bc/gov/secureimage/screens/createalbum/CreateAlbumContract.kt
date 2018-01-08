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

        fun setBacked(backed: Boolean)
        fun setRefresh(refresh: Boolean)
        fun setAlbumDeleted(albumDeleted: Boolean)

        fun showError(message: String)
        fun showMessage(message: String)

        fun setUpBackListener()

        fun showNetworkType()
        fun hideNetworkType()
        fun setNetworkTypeText(text: String)

        fun showImagesLoading()
        fun hideImagesLoading()

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)
        fun notifyImageRemoved(cameraImage: CameraImage, position: Int)

        fun setUpViewAllImagesListener()
        fun showViewAllImages()
        fun hideViewAllImages()
        fun goToAllImages(albumKey: String)

        fun showDeleteImageDialog(cameraImage: CameraImage, position: Int)
        fun hideDeleteImageDialog()

        fun setUpDeleteAlbumListener()
        fun showDeleteAlbumDialog()
        fun hideDeleteAlbumDialog()

        fun showDeletingDialog()
        fun hideDeletingDialog()

        fun setAlbumName(albumName: String)

        fun goToSecureCamera(albumKey: String)
        fun goToImageDetail(albumKey: String, imageIndex: Int)
    }

    interface Presenter: BasePresenter {
        fun viewShown(refresh: Boolean)
        fun viewHidden(backed: Boolean, albumDeleted: Boolean, albumName: String)

        fun backClicked(albumName: String)

        fun deleteAlbumClicked()
        fun deleteAlbumConfirmed()

        fun viewAllImagesClicked()

        fun addImagesClicked()

        fun imageClicked(cameraImage: CameraImage, position: Int)

        fun imageDeleteClicked(cameraImage: CameraImage, position: Int)

        fun deleteImageConfirmed(cameraImage: CameraImage, position: Int)
    }

}