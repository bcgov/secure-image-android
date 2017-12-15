package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
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
        getAlbumFields()
        getImages()
    }

    override fun viewHidden() {
        disposables.clear()
    }

    /**
     * Gets album and populates album fields
     */
    fun getAlbumFields() {
        albumsRepo.getAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving album")
                },
                onSuccess = {
                    view.setAlbumName(it.albumName)
                }
        ).addTo(disposables)
    }

    /**
     * Gets the first 5 album images and sorts by first created
     * On success show images
     */
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

    /**
     * Add all current album fields to existing album and saves
     * Update time is set to current time in millis
     * On success finishes
     */
    override fun saveClicked(albumName: String) {
        albumsRepo.getAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap {
                    it.albumName = albumName
                    it.updatedTime = System.currentTimeMillis()
                    albumsRepo.saveAlbum(it)
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving album")
                },
                onSuccess = {
                    view.showMessage("Album saved")
                    view.finish()
                })
                .addTo(disposables)
    }

    // View all
    override fun viewAllImagesClicked() {
        view.goToAllImages(albumKey)
    }

    // Add images
    override fun addImagesClicked() {
        view.goToSecureCamera(albumKey)
    }

    override fun imageClicked(cameraImage: CameraImage) {
        view.goToImageDetail(cameraImage.key)
    }
}