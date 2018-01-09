package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.common.services.NetworkService
import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.local.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class CreateAlbumPresenter(
        private val view: CreateAlbumContract.View,
        private val albumKey: String,
        private val albumsRepo: AlbumsRepo,
        private val cameraImagesRepo: CameraImagesRepo,
        private val networkService: NetworkService
) : CreateAlbumContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setBacked(false)

        view.setRefresh(true)

        view.setAlbumDeleted(false)

        view.hideNetworkType()

        view.setUpBackListener()

        view.setUpImagesList()

        view.setUpViewAllImagesListener()

        view.setUpDeleteAlbumListener()

        view.hideImagesLoading()

        view.hideAddImagesLayout()
        view.setUpAddImagesListener()

        view.setUpUploadListener()

        getAlbumFields()
    }

    override fun dispose() {
        view.hideDeleteAlbumDialog()
        view.hideDeleteImageDialog()
        view.hideDeletingDialog()
        disposables.dispose()
    }

    override fun viewShown(refresh: Boolean) {
        view.setBacked(false)

        if(refresh) {
            view.showImages(ArrayList())
            getImages()

            view.setRefresh(false)
        }

        addNetworkTypeListener()
    }

    /**
     * Clears all disposables
     * Saves album fields if back was not clicked and album is not deleted
     */
    override fun viewHidden(backed: Boolean, albumDeleted: Boolean, albumName: String) {
        disposables.clear()

        if (!backed && !albumDeleted) {
            saveAlbumFields(albumName, false)
        }
    }

    /**
     * Pings the network every 5 seconds to check if connected/disconnected or on wifi/mobile
     * Initial delay is 0 to instantly check on start up
     */
    fun addNetworkTypeListener() {
        networkService.getNetworkTypeListener(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error listening to network status")
                },
                onNext = { networkType ->
                    when (networkType) {
                        is NetworkService.NetworkType.WifiConnection -> {
                            view.hideNetworkType()
                            view.setNetworkTypeText("")
                        }
                        is NetworkService.NetworkType.MobileConnection -> {
                            view.showNetworkType()
                            view.setNetworkTypeText("Mobile connection")
                        }
                        is NetworkService.NetworkType.NoConnection -> {
                            view.showNetworkType()
                            view.setNetworkTypeText("No internet connection")
                        }
                    }
                }
        ).addTo(disposables)
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
                onSuccess = { album ->
                    view.setAlbumName(album.name)
                }
        ).addTo(disposables)
    }

    /**
     * Gets album images and sorts by first created
     * On success show images with add images model so recycler view can display an add image tile
     */
    fun getImages() {
        view.showImagesLoading()
        cameraImagesRepo.getAllCameraImagesInAlbum(albumKey)
                .flatMapIterable { it }
                .toSortedList { cameraImage1, cameraImage2 -> cameraImage1.compareTo(cameraImage2) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.hideImagesLoading()
                    view.showError(it.message ?: "Error processing images")
                },
                onSuccess = { images ->
                    view.hideImagesLoading()

                    if (images.size == 0) {
                        view.showAddImagesLayout()

                    } else {
                        view.hideAddImagesLayout()

                        val items = ArrayList<Any>()
                        items.add(AddImages())
                        items.addAll(images)
                        view.showImages(items)
                    }
                }
        ).addTo(disposables)
    }

    /**
     * Saves album fields
     */
    override fun backClicked(albumName: String) {
        view.setBacked(true)
        saveAlbumFields(albumName, true)
    }

    /**
     * Add all current album fields to existing album and saves
     * Update time is set to current time in millis
     * On success finishes
     */
    fun saveAlbumFields(albumName: String, finish: Boolean) {
        albumsRepo.getAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap { album ->
                    album.name = albumName
                    album.updatedTime = System.currentTimeMillis()
                    albumsRepo.saveAlbum(album)
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving album fields")
                },
                onSuccess = {
                    if (finish) view.finish()
                })
                .addTo(disposables)
    }

    // Delete
    override fun deleteAlbumClicked() {
        view.showDeleteAlbumDialog()
    }


    override fun deleteAlbumConfirmed() {
        deleteAlbum()
    }

    /**
     * Deletes album model then deletes all the images associated with that album
     */
    fun deleteAlbum() {
        view.showDeletingDialog()
        albumsRepo.deleteAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap { cameraImagesRepo.deleteAllCameraImagesInAlbum(albumKey) }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.hideDeletingDialog()
                    view.showError(it.message ?: "Error deleting")
                },
                onSuccess = {
                    view.setAlbumDeleted(true)
                    view.hideDeletingDialog()
                    view.showMessage("Album deleted")
                    view.finish()
                }
        ).addTo(disposables)
    }

    // View all
    override fun viewAllImagesClicked() {
        view.setRefresh(true)
        view.goToAllImages(albumKey)
    }

    // Add images
    override fun addImagesClicked() {
        view.setRefresh(true)
        view.goToSecureCamera(albumKey)
    }

    // Image click
    override fun imageClicked(cameraImage: CameraImage, position: Int) {
        view.goToImageDetail(cameraImage.albumKey, position - 1)
    }

    // Image deletion
    override fun imageDeleteClicked(cameraImage: CameraImage, position: Int) {
        view.showDeleteImageDialog(cameraImage, position)
    }

    override fun deleteImageConfirmed(cameraImage: CameraImage, position: Int) {
        deleteImage(cameraImage, position)
    }

    /**
     * Deletes camera image from local storage
     * On success notifies image list that an item has been removed
     */
    fun deleteImage(cameraImage: CameraImage, position: Int) {
        cameraImagesRepo.deleteCameraImage(cameraImage)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error deleting image")
                },
                onSuccess = { image ->
                    view.showMessage("Image deleted")
                    view.notifyImageRemoved(image, position)
                }
        ).addTo(disposables)
    }

    // Upload album
    override fun uploadClicked() {
        uploadAlbum()
    }

    fun uploadAlbum() {
        cameraImagesRepo.getAllCameraImagesInAlbum(albumKey)
                .flatMapIterable { it }
                .take(1)
                .flatMap { cameraImagesRepo.uploadCameraImage(it) }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error uploading album")
                },
                onSuccess = {
                    view.showMessage("Uploaded")
                }
        ).addTo(disposables)
    }
}