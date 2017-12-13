package ca.bc.gov.secureimage.screens.albums

import ca.bc.gov.secureimage.data.models.Album

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class AlbumsPresenter(
        private val view: AlbumsContract.View
) : AlbumsContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpAlbumsList()
        getAlbums()

        view.setUpCreateAlbumListener()
    }

    override fun dispose() {

    }

    override fun createAlbumClicked() {
        view.goToCreateAlbum()
    }

    fun getAlbums() {
        val albums = ArrayList<Any>()
        albums.add(Album())
        albums.add(Album())
        albums.add(Album())
        albums.add(Album())
        albums.add(Album())
        albums.add(Album())
        view.showAlbumItems(albums)
    }

}