package ca.bc.gov.secureimage.screens.allimages

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class AllImagesPresenter(
        private val view: AllImagesContract.View,
        private val albumKey: String,
        private val albumsRepo: AlbumsRepo
) : AllImagesContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpImagesList()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
        view.showImages(ArrayList())
        getImages()
    }

    override fun viewHidden() {
        disposables.clear()
    }

    fun getImages() {
        albumsRepo.getAlbum(albumKey)
                .map { it.cameraImages }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving images")
                },
                onSuccess = {
                    val items = ArrayList<Any>()
                    items.add(AddImages())
                    items.addAll(it)
                    view.showImages(items)
                }
        ).addTo(disposables)
    }

    // Back
    override fun backClicked() {
        view.finish()
    }

    // Add images
    override fun addImagesClicked() {
        view.goToSecureCamera(albumKey)
    }

    // Image clicks
    override fun imageClicked(cameraImage: CameraImage) {
        view.goToImageDetail(cameraImage.key)
    }
}