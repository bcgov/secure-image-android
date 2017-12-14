package ca.bc.gov.securecamera.view

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

        // Set to 2 so only camera permission is called and audio permission is not called
        view.setCameraPermissions(2)

        view.setUpCameraListener()

        view.hideCaptureImage()
        view.setUpCaptureImageListener()

        view.hideBack()
        view.setUpBackListener()

        view.hideDone()
        view.setUpDoneListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
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
            }
        }
    }

    override fun onCameraError(error: CameraKitError?) {
        val message = "Error: ${error?.exception.toString()}"
        println(message)
    }

    override fun onCameraImage(image: CameraKitImage?) {
        view.showMessage("Image Captured")
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
}