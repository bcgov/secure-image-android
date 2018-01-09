package ca.bc.gov.secureimage.screens.securecamera

import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.services.CompressionService
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import com.github.florent37.rxgps.RxGps
import com.wonderkiln.camerakit.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class SecureCameraPresenter(
        private val view: SecureCameraContract.View,
        private val albumKey: String,
        private val cameraImagesRepo: CameraImagesRepo,
        private val albumsRepo: AlbumsRepo,
        private val locationRepo: LocationRepo,
        private val rxGps: RxGps,
        private val compressionService: CompressionService
) : SecureCameraContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setCapturing(false)

        view.hideShutter()

        view.setCameraMethod(CameraKit.Constants.METHOD_STILL)
        view.setCameraFlash(CameraKit.Constants.FLASH_OFF)
        view.showFlashOff()

        view.setCameraCropOutput(false)
        view.setCameraPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        view.setCameraFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER)
        view.setPinchToZoom(true)

        view.setUpCameraListener()

        view.setUpCaptureImageListener()

        view.setUpBackListener()

        view.setUpFlashControlListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
        getLocationAndCache()
        getAlbumImageCount()

        view.setCapturing(false)

        view.hideShutter()
        view.hideCaptureImage()
        view.hideBack()
        view.hideFlashControl()
        view.hideImageCounter()

        view.startCamera()
    }

    override fun viewHidden() {
        view.stopCamera()
    }

    /**
     * Grabs user location and caches it in location repo
     */
    fun getLocationAndCache() {
        locationRepo.getLocation(rxGps, true)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(onError = {})
                .addTo(disposables)
    }

    /**
     * Tells view to capture image
     * Shows shutter to indicate that an image is being taken
     * Shutter is hidden after an image is returned in onCameraImage
     */
    override fun captureImageClicked(capturing: Boolean) {
        if (!capturing) {
            view.setCapturing(true)
            view.showShutter()
            view.captureImage()
        }
    }

    // Camera listener events
    override fun onCameraEvent(event: CameraKitEvent?) {
        when (event?.type) {
            CameraKitEvent.TYPE_CAMERA_OPEN -> {
                view.showCaptureImage()
                view.showBack()
                view.showFlashControl()
                view.showImageCounter()
            }
        }
    }

    override fun onCameraError(error: CameraKitError?) {

    }

    override fun onCameraImage(image: CameraKitImage?) {
        view.hideShutter()

        if (image == null) {
            view.setCapturing(false)
            return
        }

        createCameraImage(image.jpeg, 100, 1920, 100, 300)
    }

    /**
     * Creates camera image with album key
     * Calls to compress image
     */
    fun createCameraImage(
            imageBytes: ByteArray,
            imageQuality: Int,
            imageSize: Int,
            thumbnailQuality: Int,
            thumbnailSize: Int) {

        val cameraImage = CameraImage()
        cameraImage.albumKey = albumKey

        compressImageBytesForCameraImage(
                cameraImage, imageBytes, imageQuality, imageSize, thumbnailQuality, thumbnailSize)
    }

    /**
     * Compresses image byte array
     * On success creates camera image object and calls to get a location for it
     */
    fun compressImageBytesForCameraImage(
            cameraImage: CameraImage,
            imageBytes: ByteArray,
            imageQuality: Int,
            imageSize: Int,
            thumbnailQuality: Int,
            thumbnailSize: Int) {

        Observable.zip(
                compressionService.compressByteArrayAsObservable(
                        imageBytes, imageQuality, imageSize, imageSize),
                compressionService.compressByteArrayAsObservable(
                        imageBytes, thumbnailQuality, thumbnailSize, thumbnailSize),
                BiFunction { compressedImageBytes: ByteArray, thumbnailBytes: ByteArray ->
                    Pair<ByteArray, ByteArray>(compressedImageBytes, thumbnailBytes)
                })
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.setCapturing(false)
                    view.showError(it.message ?: "Error compressing image")
                },
                onSuccess = { byteArrayPair ->
                    cameraImage.imageByteArray = byteArrayPair.first
                    cameraImage.thumbnailArray = byteArrayPair.second
                    getLocationForCameraImage(cameraImage)
                }
        ).addTo(disposables)
    }

    /**
     * Grabs the location of device
     * On error saves camera image without lat lon
     * On success saves camera image with lat lon
     * On complete saves camera image without lat lon
     */
    fun getLocationForCameraImage(cameraImage: CameraImage) {
        locationRepo.getCachedLocation()
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    saveCameraImage(cameraImage)
                },
                onSuccess = { location ->
                    cameraImage.lat = location.lat
                    cameraImage.lon = location.lon
                    saveCameraImage(cameraImage)
                },
                onComplete = {
                    saveCameraImage(cameraImage)
                }
        ).addTo(disposables)
    }

    /**
     * Saves camera image locally
     * Checks to see if current album count is less than the limit before saving
     * On success gets album image count and refreshes album update time
     */
    fun saveCameraImage(cameraImage: CameraImage) {
        cameraImagesRepo.getCameraImageCountInAlbum(albumKey)
                .flatMap { albumSize ->
                    if (albumSize < Constants.MAX_ALBUM_SIZE) {
                        cameraImagesRepo.saveCameraImage(cameraImage)
                    } else {
                        val message = "Can not take more than ${Constants.MAX_ALBUM_SIZE} images per album"
                        Observable.error(Throwable(message))
                    }
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.setCapturing(false)
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = {
                    view.setCapturing(false)
                    getAlbumImageCount()
                    refreshAlbumUpdateTime()
                }
        ).addTo(disposables)
    }

    /**
     * Gets the current count of images in album
     * onSuccess current counter is shown
     */
    fun getAlbumImageCount() {
        cameraImagesRepo.getCameraImageCountInAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = { albumSize ->
                    val imageCounterText = "$albumSize ${if (albumSize == 1) "Image" else "Images"}"
                    view.setImageCounterText(imageCounterText)
                }
        ).addTo(disposables)
    }

    fun refreshAlbumUpdateTime() {
        albumsRepo.getAlbum(albumKey)
                .observeOn(Schedulers.io())
                .flatMap { album ->
                    album.updatedTime = System.currentTimeMillis()
                    albumsRepo.saveAlbum(album)
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving album")
                })
                .addTo(disposables)
    }

    override fun onCameraVideo(video: CameraKitVideo?) {

    }

    // Back
    override fun backClicked() {
        view.finish()
    }

    // Flash control
    override fun flashControlClicked(flashMode: Int) {
        when (flashMode) {
            CameraKit.Constants.FLASH_OFF -> {
                view.showFlashOff()
                view.setCameraMethod(CameraKit.Constants.METHOD_STILL)
            }
            CameraKit.Constants.FLASH_ON -> {
                view.showFlashOn()
                view.setCameraMethod(CameraKit.Constants.METHOD_STANDARD)
            }
            CameraKit.Constants.FLASH_AUTO -> {
                view.showFlashAuto()
                view.setCameraMethod(CameraKit.Constants.METHOD_STANDARD)
            }
        }
    }
}