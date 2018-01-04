package ca.bc.gov.secureimage.screens.createalbum

import ca.bc.gov.secureimage.common.services.NetworkService
import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.CameraImage
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
        view.hideNetworkType()

        view.setUpBackListener()

        view.setUpImagesList()

        view.setUpViewAllImagesListener()

        view.setUpDeleteListener()

        view.setRefresh(true)

        getAlbumFields()
    }

    override fun dispose() {
        view.hideDeleteAlbumDialog()
        disposables.dispose()
    }

    override fun viewShown(refresh: Boolean) {
        if(refresh) {
            view.showImages(ArrayList())
            getImages()

            view.setRefresh(false)
        }

        addNetworkTypeListener()
    }

    override fun viewHidden(albumName: String) {
        disposables.clear()
        saveAlbumFields(albumName)
    }

    /**
     * Pings the network every 5 seconds to check if connected/disconnected or on wifi/mobile
     */
    fun addNetworkTypeListener() {
        networkService.getNetworkTypeListener(0, 5, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error listening to network status")
                },
                onNext = {
                    when (it) {
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
                onSuccess = {
                    view.setAlbumName(it.name)
                }
        ).addTo(disposables)
    }

    /**
     * Gets the first 5 album images and sorts by first created
     * On success show images with add images model so recycler view can display an add image tile
     */
    fun getImages() {
        cameraImagesRepo.getCameraImagesInAlbum(albumKey, 5)
                .flatMapIterable { it }
                .toSortedList { cameraImage1, cameraImage2 -> cameraImage1.compareTo(cameraImage2) }
                .subscribeOn(Schedulers.io())
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
     */
    fun saveAlbumFields(albumName: String) {
        albumsRepo.getAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap {
                    it.name = albumName
                    it.updatedTime = System.currentTimeMillis()
                    albumsRepo.saveAlbum(it)
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = {})
                .addTo(disposables)
    }

    // Delete
    override fun deleteClicked() {
        view.showDeleteAlbumDialog()
    }

    /**
     * Deletes album model then deletes all the images associated with that album
     */
    override fun deleteConfirmed() {
        albumsRepo.deleteAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap { cameraImagesRepo.deleteAllCameraImagesInAlbum(albumKey) }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error deleting")
                },
                onSuccess = {
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

    override fun imageClicked(cameraImage: CameraImage, position: Int) {
        view.goToImageDetail(cameraImage.albumKey, position - 1)
    }
}