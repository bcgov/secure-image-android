package ca.bc.gov.secureimage.screens.albums

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView
import ca.bc.gov.secureimage.data.models.local.Album

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface AlbumsContract {

    interface View: BaseView<Presenter> {
        fun showError(message: String)

        fun showLoading()
        fun hideLoading()

        fun setUpAlbumsList()
        fun showAlbumItems(items: ArrayList<Any>)

        fun setUpCreateAlbumListener()
        fun goToCreateAlbum(albumKey: String)

        fun showOnboarding()
        fun hideOnboarding()
    }

    interface Presenter: BasePresenter {
        fun viewShown()
        fun viewHidden()

        fun createAlbumClicked()

        fun albumClicked(album: Album)
    }

}