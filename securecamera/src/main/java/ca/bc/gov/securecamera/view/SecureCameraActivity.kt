package ca.bc.gov.securecamera.view

import android.graphics.SurfaceTexture
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.TextureView
import android.view.WindowManager
import ca.bc.gov.securecamera.R
import kotlinx.android.synthetic.main.activity_secure_camera.*

class SecureCameraActivity : AppCompatActivity(), SecureCameraContract.View, TextureView.SurfaceTextureListener {

    override var presenter: SecureCameraContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status Bar Color
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
        setContentView(R.layout.activity_secure_camera)

        SecureCameraPresenter(this)
        presenter?.subscribe()

        textureView.surfaceTextureListener = this
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Surface texture listener
    override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture?, p1: Int, p2: Int) {

    }

    override fun onSurfaceTextureUpdated(p0: SurfaceTexture?) {

    }

    override fun onSurfaceTextureDestroyed(p0: SurfaceTexture?): Boolean {

    }

    override fun onSurfaceTextureAvailable(p0: SurfaceTexture?, p1: Int, p2: Int) {
        
    }

}
