package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.Image

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class CreateAlbumPresenter(
        private val view: CreateAlbumContract.View
) : CreateAlbumContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpImagesList()
        getImages()

        view.setUpViewAllImagesListener()

        view.setUpUploadListener()
    }

    override fun dispose() {

    }

    fun getImages() {
        val images = ArrayList<Any>()
        images.add(AddImages())
        images.add(Image())
        images.add(Image())
        images.add(Image())
        images.add(Image())
        images.add(Image())
        view.showImages(images)
    }

    override fun backClicked() {
        view.finish()
    }

    override fun uploadClicked() {

    }

    override fun viewAllImagesClicked() {
        view.goToAllImages()
    }

    override fun addImagesClicked() {
        view.goToSecureCamera()
    }
}