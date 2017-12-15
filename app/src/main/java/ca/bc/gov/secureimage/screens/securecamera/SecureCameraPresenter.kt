package ca.bc.gov.secureimage.screens.securecamera

import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import com.wonderkiln.camerakit.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
        private val cameraImagesRepo: CameraImagesRepo
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

        getAlbumImageCount()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
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

        val cameraImage = CameraImage()
        cameraImage.byteArray = image.jpeg
        cameraImage.albumKey = albumKey
        saveCameraImage(cameraImage)
    }

    /**
     * Gets the current count of images in album
     * onSuccess current counter is shown
     */
    private fun getAlbumImageCount() {
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

    /**
     * Saves camera image locally then grabs the current amount of images for the album
     * On success gets called with the integer size of images and the image counter is displayed
     */
    private fun saveCameraImage(cameraImage: CameraImage) {
        cameraImagesRepo.saveCameraImage(cameraImage)
                .observeOn(Schedulers.io())
                .flatMap { cameraImagesRepo.getImageCountInAlbum(albumKey) }
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