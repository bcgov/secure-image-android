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

        fun setRefresh(refresh: Boolean)

        fun showLoading()
        fun hideLoading()

        fun showMessage(message: String)
        fun showError(message: String)

        fun setToolbarColorPrimary()
        fun setToolbarColorPrimaryLight()

        fun showBack()
        fun hideBack()
        fun setUpBackListener()

        fun showToolbarTitle()
        fun hideToolbarTitle()

        fun showSelect()
        fun hideSelect()
        fun setUpSelectListener()

        fun showSelectClose()
        fun hideSelectClose()
        fun setUpSelectCloseListener()

        fun showSelectTitle()
        fun hideSelectTitle()
        fun setSelectTitleSelectItems()
        fun setSelectTitle(title: String)

        fun showSelectDelete()
        fun hideSelectDelete()
        fun setUpSelectDeleteListener()

        fun setUpImagesList()
        fun showImages(items: ArrayList<Any>)
        fun setSelectMode(selectMode: Boolean)
        fun itemChanged(position: Int)
        fun clearSelectedImages()

        fun goToSecureCamera(albumKey: String)
        fun goToImageDetail(albumKey: String, imageIndex: Int)
    }

    interface Presenter: BasePresenter {
        fun viewShown(refresh: Boolean)
        fun viewHidden()

        fun backClicked()

        fun selectClicked()

        fun closeSelectClicked()

        fun selectDeleteClicked(cameraImages: ArrayList<CameraImage>)

        fun addImagesClicked()

        fun imageClicked(cameraImage: CameraImage, position: Int)

        fun imageSelected(cameraImage: CameraImage, position: Int, selectedCount: Int)

        fun imageLongClicked(cameraImage: CameraImage, position: Int, selectedCount: Int)
    }

}