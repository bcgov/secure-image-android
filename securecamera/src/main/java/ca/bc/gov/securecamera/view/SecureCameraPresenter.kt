package ca.bc.gov.securecamera.view

import ca.bc.gov.securecamera.data.ImagesHolder
import com.wonderkiln.camerakit.*
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class SecureCameraPresenter(
        private val view: SecureCameraContract.View
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
        ImagesHolder.addImage(image.jpeg)

        val imagesSize = ImagesHolder.getAllImages().size
        val imageCounterText = "${imagesSize} ${if (imagesSize == 1) "Image" else "Images"}"
        view.setImageCounterText(imageCounterText)
        view.showImageCounter()
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