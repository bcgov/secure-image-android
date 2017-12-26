package ca.bc.gov.secureimage.screens.securecamera

import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import com.github.florent37.rxgps.RxGps
import com.wonderkiln.camerakit.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ca.bc.gov.secureimage.common.utils.CompressionUtils

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class SecureCameraPresenter(
        private val view: SecureCameraContract.View,
        private val albumKey: String,
        private val cameraImagesRepo: CameraImagesRepo,
        private val locationRepo: LocationRepo,
        private val rxGps: RxGps
) : SecureCameraContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setCameraMethod(CameraKit.Constants.METHOD_STANDARD)
        view.setCameraCropOutput(false)
        view.setCameraPermissions(CameraKit.Constants.PERMISSIONS_PICTURE)
        view.setCameraFocus(CameraKit.Constants.FOCUS_TAP_WITH_MARKER)
        view.setPinchToZoom(true)

        view.setUpCameraListener()

        view.setUpCaptureImageListener()

        view.setUpBackListener()

        view.setUpDoneListener()

        view.setCameraFlash(CameraKit.Constants.FLASH_OFF)
        view.showFlashOff()
        view.setUpFlashControlListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
        getLocationAndCache()
        getAlbumImageCount()

        view.hideCaptureImage()
        view.hideBack()
        view.hideDone()
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
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = { view.showError(it.message ?: "Error retrieving location") },
                onSuccess = { })
                .addTo(disposables)
    }

    // Camera listener events
    override fun onCameraEvent(event: CameraKitEvent?) {
        when (event?.type) {
            CameraKitEvent.TYPE_CAMERA_OPEN -> {
                view.showCaptureImage()
                view.showBack()
                view.showDone()
                view.showFlashControl()
            }
        }
    }

    override fun onCameraError(error: CameraKitError?) {

    }

    override fun onCameraImage(image: CameraKitImage?) {
        if (image == null) return

        createCameraImage(image.jpeg, 50, 1080, 1920)
    }

    /**
     * Creates camera image with album key
     * Calls to compress image
     */
    fun createCameraImage(imageBytes: ByteArray, quality: Int, reqWidth: Int, reqHeight: Int) {
        val cameraImage = CameraImage()
        cameraImage.albumKey = albumKey

        compressImageBytesForCameraImage(cameraImage, imageBytes, quality, reqWidth, reqHeight)
    }

    /**
     * Compresses image byte array
     * On success creates camera image object and calls to get a location for it
     */
    fun compressImageBytesForCameraImage(
            cameraImage: CameraImage,
            imageBytes: ByteArray,
            quality: Int,
            reqWidth: Int,
            reqHeight: Int) {

        val compressImageObservable = Observable.create<ByteArray> { emitter ->
            emitter.onNext(CompressionUtils.compressToJpeg(imageBytes, quality, reqWidth, reqHeight))
            emitter.onComplete()
        }

        compressImageObservable
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error compressing image")
                },
                onSuccess = {
                    cameraImage.byteArray = it
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
                onSuccess = {
                    cameraImage.lat = it.lat
                    cameraImage.lon = it.lon
                    saveCameraImage(cameraImage)
                },
                onComplete = {
                    saveCameraImage(cameraImage)
                }
        ).addTo(disposables)
    }

    /**
     * Saves camera image locally
     * On success gets album image count
     */
    fun saveCameraImage(cameraImage: CameraImage) {
        cameraImagesRepo.saveCameraImage(cameraImage)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = {
                    getAlbumImageCount()
                }
        ).addTo(disposables)
    }

    /**
     * Gets the current count of images in album
     * onSuccess current counter is shown
     */
    fun getAlbumImageCount() {
        cameraImagesRepo.getImageCountInAlbum(albumKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving image")
                },
                onSuccess = {
                    val imageCounterText = "$it ${if (it == 1) "Image" else "Images"}"
                    view.setImageCounterText(imageCounterText)
                    view.showImageCounter()
                }
        ).addTo(disposables)
    }

    override fun onCameraVideo(video: CameraKitVideo?) {

    }

    // Take image
    override fun takeImageClicked() {
        view.captureImage()
    }

    // Back
    override fun backClicked() {
        view.finish()
    }

    // Done
    override fun doneClicked() {
        view.finish()
    }

    // Flash control
    override fun flashControlClicked(flashMode: Int) {
        when (flashMode) {
            CameraKit.Constants.FLASH_OFF -> view.showFlashOff()
            CameraKit.Constants.FLASH_ON -> view.showFlashOn()
            CameraKit.Constants.FLASH_AUTO -> view.showFlashAuto()
        }
    }
}