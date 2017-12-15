package ca.bc.gov.secureimage.screens.imagedetail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.di.Injection
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_image_detail.*

class ImageDetailActivity : AppCompatActivity(), ImageDetailContract.View {

    override var presenter: ImageDetailContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        // Extras
        val imageKey = intent.getStringExtra(Constants.IMAGE_KEY)
        if(imageKey == null) {
            showError("No image key passed")
            finish()
            return
        }

        ImageDetailPresenter(
                this,
                imageKey,
                Injection.provideCameraImagesRepo())

        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showImage(byteArray: ByteArray) {
        Glide.with(this)
                .load(byteArray)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageIv)
    }
}
