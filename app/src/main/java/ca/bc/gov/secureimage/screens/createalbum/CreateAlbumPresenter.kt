package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import io.reactivex.Observable
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
        private val albumKey: String,
        private val albumsRepo: AlbumsRepo
) : CreateAlbumContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpImagesList()

        view.setUpViewAllImagesListener()

        view.setUpSaveListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
        view.showImages(ArrayList())
        getAlbum()
        getImages()
    }

    override fun viewHidden() {
        disposables.clear()
    }

    // Album
    fun getAlbum() {
        albumsRepo.getAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving album")
                },
                onSuccess = {

                }
        ).addTo(disposables)
    }

    // Images
    fun getImages() {
        albumsRepo.getAlbum(albumKey)
                .map { it.cameraImages }
                .flatMapIterable { it }
                .take(5)
                .toSortedList { cameraImage1, cameraImage2 -> cameraImage1.compareTo(cameraImage2) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error processing images")
                },
                onSuccess = {
                    val items = ArrayList<Any>()
                    items.add(AddImages())
                    items.addAll(it)
                    view.showImages(items)
                }
        ).addTo(disposables)
    }

    // back
    override fun backClicked() {
        view.finish()
    }

    // Save
    override fun saveClicked() {

    }

    // View all
    override fun viewAllImagesClicked() {
        view.goToAllImages(albumKey)
    }

    // Add images
    override fun addImagesClicked() {
        view.goToSecureCamera(albumKey)
    }
}