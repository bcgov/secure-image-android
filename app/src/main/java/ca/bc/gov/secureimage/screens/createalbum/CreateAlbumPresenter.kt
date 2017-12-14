package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.securecamera.data.CameraImagesRepo
import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.Image
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class CreateAlbumPresenter(
        private val view: CreateAlbumContract.View,
        private val cameraImagesRepo: CameraImagesRepo
) : CreateAlbumContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpImagesList()
        getImages()

        view.setUpViewAllImagesListener()

        view.setUpSaveListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    /**
     * Gets firs 5 camera images from camera repo and turns them into images
     * For each of the images creates a scaled bitmap version
     * On success shows the list of image items
     */
    fun getImages() {
        cameraImagesRepo.getAllImages()
                .flatMapIterable { it }
                .take(5)
                .map { Image(it.byteArray) }
                .map {
                    it.createScaledBitmap(500)
                    it
                }
                .toList()
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

    override fun backClicked() {
        view.finish()
    }

    override fun saveClicked() {

    }

    override fun viewAllImagesClicked() {
        view.goToAllImages()
    }

    override fun addImagesClicked() {
        view.goToSecureCamera()
    }
}