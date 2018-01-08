package ca.bc.gov.secureimage.screens.securecamera

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import com.github.florent37.rxgps.RxGps
import com.wonderkiln.camerakit.*
import kotlinx.android.synthetic.main.activity_secure_camera.*

class SecureCameraActivity : AppCompatActivity(), SecureCameraContract.View, CameraKitEventListener {

    override var presenter: SecureCameraContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_secure_camera)

        // Extras
        val albumKey = intent.getStringExtra(Constants.ALBUM_KEY)
        if(albumKey == null) {
            showError("No Album key passed")
            finish()
            return
        }

        SecureCameraPresenter(this,
                albumKey,
                Injection.provideCameraImagesRepo(),
                Injection.provideLocationRepo(),
                RxGps(this),
                Injection.provideCompressionService()
        )

        presenter?.subscribe()
    }

    override fun onResume() {
        super.onResume()
        presenter?.viewShown()
    }

    override fun onPause() {
        super.onPause()
        presenter?.viewHidden()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Message
    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Camera method
    override fun setCameraMethod(cameraMethod: Int) {
        cameraView.setMethod(cameraMethod)
    }

    // Crop output
    override fun setCameraCropOutput(cropOutput: Boolean) {
        cameraView.setCropOutput(cropOutput)
    }

    // Camera Permissions
    override fun setCameraPermissions(permissions: Int) {
        cameraView.setPermissions(permissions)
    }

    // Focus
    override fun setCameraFocus(focus: Int) {
        cameraView.setFocus(focus)
    }

    // Pinch to zoom
    override fun setPinchToZoom(pinchToZooom: Boolean) {
        cameraView.setPinchToZoom(pinchToZooom)
    }

    // Camera lifecycle
    override fun startCamera() {
        cameraView.start()
    }

    override fun stopCamera() {
        cameraView.stop()
    }

    // Camera listener
    override fun setUpCameraListener() {
        cameraView.addCameraKitListener(this)
    }

    override fun onEvent(event: CameraKitEvent?) {
        presenter?.onCameraEvent(event)
    }

    override fun onError(error: CameraKitError?) {
        presenter?.onCameraError(error)
    }

    override fun onImage(image: CameraKitImage?) {
        presenter?.onCameraImage(image)
    }

    override fun onVideo(video: CameraKitVideo?) {
        presenter?.onCameraVideo(video)
    }

    // Capture image
    override fun setUpCaptureImageListener() {
        captureImageIv.setOnClickListener {
            presenter?.takeImageClicked()
        }
    }

    override fun showCaptureImage() {
        captureImageIv.visibility = View.VISIBLE
    }

    override fun hideCaptureImage() {
        captureImageIv.visibility = View.GONE
    }

    override fun captureImage() {
        cameraView.captureImage()
    }

    // Back
    override fun setUpBackListener() {
        backIv.setOnClickListener {
            presenter?.backClicked()
        }
    }

    override fun showBack() {
        backIv.visibility = View.VISIBLE
    }

    override fun hideBack() {
        backIv.visibility = View.GONE
    }

    // Flash control
    override fun setCameraFlash(flash: Int) {
        cameraView.flash = flash
    }

    override fun setUpFlashControlListener() {
        flashControlTv.setOnClickListener {
            cameraView.toggleFlash()
            presenter?.flashControlClicked(cameraView.flash)
        }
    }

    override fun showFlashControl() {
        flashControlTv.visibility = View.VISIBLE
    }

    override fun hideFlashControl() {
        flashControlTv.visibility = View.GONE
    }

    override fun showFlashOff() {
        flashControlTv.setText(R.string.flash_off)
        flashControlTv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_flash_off_white_24dp, 0, 0, 0)
    }

    override fun showFlashOn() {
        flashControlTv.setText(R.string.flash_on)
        flashControlTv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_flash_on_white_24dp, 0, 0, 0)
    }

    override fun showFlashAuto() {
        flashControlTv.setText(R.string.flash_auto)
        flashControlTv.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_flash_auto_white_24dp, 0, 0, 0)
    }

    // Image counter
    override fun setImageCounterText(text: String) {
        imageCounterTv.text = text
    }

    override fun showImageCounter() {
        imageCounterTv.visibility = View.VISIBLE
    }

    override fun hideImageCounter() {
        imageCounterTv.visibility = View.GONE
    }
}

