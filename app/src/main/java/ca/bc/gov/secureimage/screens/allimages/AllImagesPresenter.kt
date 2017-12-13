package ca.bc.gov.secureimage.screens.allimages

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.Image

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class AllImagesPresenter(
        private val view: AllImagesContract.View
) : AllImagesContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpImagesList()
        getImages()
    }

    override fun dispose() {

    }

    fun getImages() {
        val items = ArrayList<Any>()
        items.add(AddImages())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        items.add(Image())
        view.showImages(items)
    }

    // Back
    override fun backClicked() {
        view.finish()
    }

    // Add images
    override fun addImagesClicked() {
        view.goToSecureCamera()
    }
}