package ca.bc.gov.securecamera.view

import ca.bc.gov.securecamera.common.base.BasePresenter
import ca.bc.gov.securecamera.common.base.BaseView
import com.wonderkiln.camerakit.*

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
interface SecureCameraContract {

    interface View : BaseView<Presenter> {
        fun finish()

        fun showMessage(message: String)
        fun showError(message: String)

        fun setCameraMethod(cameraMethod: Int)
        fun setCameraCropOutput(cropOutput: Boolean)
        fun setCameraPermissions(permissions: Int)
        fun setCameraFocus(focus: Int)
        fun setPinchToZoom(pinchToZooom: Boolean)

        fun startCamera()
        fun stopCamera()

        fun setUpCameraListener()

        fun setUpCaptureImageListener()
        fun showCaptureImage()
        fun hideCaptureImage()
        fun captureImage()

        fun setUpBackListener()
        fun showBack()
        fun hideBack()

        fun setUpDoneListener()
        fun showDone()
        fun hideDone()

        fun setCameraFlash(flash: Int)
        fun setUpFlashControlListener()
        fun showFlashControl()
        fun hideFlashControl()
        fun showFlashOff()
        fun showFlashOn()
        fun showFlashAuto()

        fun setImageCounterText(text: String)
        fun showImageCounter()
        fun hideImageCounter()
    }

    interface Presenter : BasePresenter {
        fun viewShown()
        fun viewHidden()

        fun onCameraEvent(event: CameraKitEvent?)
        fun onCameraError(error: CameraKitError?)
        fun onCameraImage(image: CameraKitImage?)
        fun onCameraVideo(video: CameraKitVideo?)

        fun takeImageClicked()

        fun backClicked()

        fun doneClicked()

        fun flashControlClicked(flashMode: Int)
    }

}