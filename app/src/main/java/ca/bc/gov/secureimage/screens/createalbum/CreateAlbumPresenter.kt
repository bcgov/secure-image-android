package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.common.managers.NetworkManager
import ca.bc.gov.secureimage.data.AppApi
import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.local.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.user.UserRepo
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
        private val networkManager: NetworkManager,
        private val appApi: AppApi,
        private val userRepo: UserRepo
) : CreateAlbumContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.hideNetworkType()

        view.setBacked(false)
        view.setUpBackListener()

        view.setAlbumDeleted(false)
        view.setUpDeleteAlbumListener()

        view.setRefresh(true)
        view.setUpAddImagesListener()
        view.hideImagesLoading()
        view.setUpImagesList()
        view.setUpViewAllImagesListener()

        view.setUpUploadListener()

        getAlbumFields()
    }

    override fun dispose() {
        view.hideDeleteAlbumDialog()
        view.hideDeleteImageDialog()
        view.hideDeletingDialog()
        view.hideUploadingDialog()
        view.hideMobileNetworkWarningDialog()
        view.hideNoConnectionDialog()

        disposables.dispose()
    }

    override fun viewShown(refresh: Boolean) {
        view.setBacked(false)

        if (refresh) {
            view.hideAddImagesLayout()
            view.hideViewAllImages()
            view.hideUpload()
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
        networkManager.getNetworkTypeListener(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error listening to network status")
                },
                onNext = { networkType ->
                    when (networkType) {
                        is NetworkManager.NetworkType.WifiConnection -> {
                            view.hideNetworkType()
                            view.setNetworkTypeText("")
                        }
                        is NetworkManager.NetworkType.MobileConnection -> {
                            view.showNetworkType()
                            view.setNetworkTypeText("Mobile connection")
                        }
                        is NetworkManager.NetworkType.NoConnection -> {
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
                    albumSizeReturned(images.size)

                    if (images.size > 0) {
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

    // Delete album
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

    private fun getViewAllText(albumSize: Int): String {
        return "View all Images ($albumSize)"
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
                    checkAlbumImageCountForNewState()
                }
        ).addTo(disposables)
    }

    /**
     * Gets the current count of images in album to check to see
     */
    fun checkAlbumImageCountForNewState() {
        cameraImagesRepo.getCameraImageCountInAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = { albumSize ->
                    albumSizeReturned(albumSize)
                }
        ).addTo(disposables)
    }

    // Updates state of view based on if the album is empty or not
    fun albumSizeReturned(albumSize: Int) {
        if (albumSize == 0) {
            view.showAddImagesLayout()
            view.hideViewAllImages()
            view.hideUpload()

        } else {
            view.hideAddImagesLayout()
            view.showViewAllImages()
            view.setViewAllImagesText(getViewAllText(albumSize))
            view.showUpload()
        }
    }

    // Upload album
    override fun uploadClicked() {
        getNetworkTypeForUpload()
    }

    /**
     * Grabs the current network type to determine if upload is
     * viable, a warning needs to be showed, or not possible
     */
    fun getNetworkTypeForUpload() {
        val networkType = networkManager.getNetworkType()
        when (networkType) {
            is NetworkManager.NetworkType.WifiConnection -> getCameraImageCountForUploadingDialog()
            is NetworkManager.NetworkType.MobileConnection -> view.showMobileNetworkWarningDialog()
            is NetworkManager.NetworkType.NoConnection -> view.showNoConnectionDialog()
        }
    }

    override fun uploadAnywayClicked() {
        getCameraImageCountForUploadingDialog()
    }

    /**
     * Gets the image count to populate the uploading dialog with the number of images to be
     * uploaded.
     */
    fun getCameraImageCountForUploadingDialog() {
        cameraImagesRepo.getCameraImageCountInAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = { albumSize ->
                    view.showUploadingDialog(albumSize)
                    createRemoteAlbumId()
                }
        ).addTo(disposables)
    }

    /**
     * Gets a remote album id that can be used to upload images to and build a download url
     */
    fun createRemoteAlbumId() {
        appApi.createRemoteAlbumId()
                .map { it.remoteAlbumId }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error getting remote album id")
                    view.hideUploadingDialog()
                },
                onSuccess = { remoteAlbumId ->
                    uploadImagesToRemoteAlbum(remoteAlbumId)
                }
        ).addTo(disposables)
    }

    /**
     * Uploads all the images to the specified remote album id
     * Updates uploaded count in uploading dialog after each upload has finished
     * Builds download url once all images have been uploaded
     */
    fun uploadImagesToRemoteAlbum(remoteAlbumId: String) {
        cameraImagesRepo.getAllCameraImagesInAlbum(albumKey)
                .flatMapIterable { it }
                .flatMap { cameraImagesRepo.uploadCameraImage(remoteAlbumId, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error uploading album")
                    view.hideUploadingDialog()
                },
                onNext = {
                    view.incrementUploadedCount()
                },
                onComplete = {
                    buildDownloadUrl(remoteAlbumId)
                }
        ).addTo(disposables)
    }

    /**
     * Gets a download url which will download a zip file of all the uploaded image associated with
     * that remote album id.
     */
    fun buildDownloadUrl(remoteAlbumId: String) {
        appApi.buildDownloadUrl(remoteAlbumId)
                .map { it.downloadUrl }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error getting download url")
                    view.hideUploadingDialog()
                },
                onSuccess = { downloadUrl ->
                    getUserForEmailChooser(downloadUrl)
                }
        ).addTo(disposables)
    }

    /**
     * Gets the current user for it's email.
     * Shows email chooser on success
     */
    fun getUserForEmailChooser(downloadUrl: String) {
        userRepo.getUser()
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error getting user")
                    view.hideUploadingDialog()
                },
                onSuccess = {
                    view.showEmailChooser(
                            it.email,
                            "Secure image",
                            downloadUrl,
                            "Send download link using....")
                    view.hideUploadingDialog()
                }
        ).addTo(disposables)
    }
}