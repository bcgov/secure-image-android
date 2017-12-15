package ca.bc.gov.secureimage.screens.allimages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.widget.Toast
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.screens.securecamera.SecureCameraActivity
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.adapters.images.AddImagesViewHolder
import ca.bc.gov.secureimage.common.adapters.images.ImageViewHolder
import ca.bc.gov.secureimage.common.adapters.images.ImagesAdapter
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.screens.imagedetail.ImageDetailActivity
import kotlinx.android.synthetic.main.activity_all_images.*

class AllImagesActivity : AppCompatActivity(), AllImagesContract.View, AddImagesViewHolder.Listener, ImageViewHolder.ImageClickListener {

    override var presenter: AllImagesContract.Presenter? = null

    private var imagesAdapter: ImagesAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_images)

        // Extras
        val albumKey = intent.getStringExtra(Constants.ALBUM_KEY)
        if(albumKey == null) {
            showError("No Album key passed")
            finish()
            return
        }

        AllImagesPresenter(this,
                albumKey,
                Injection.provideCameraImagesRepo())

        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    override fun onResume() {
        super.onResume()
        presenter?.viewShown()
    }

    override fun onPause() {
        super.onPause()
        presenter?.viewHidden()
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Back
    override fun setUpBackListener() {
        backIv.setOnClickListener {
            presenter?.backClicked()
        }
    }

    // Images list
    override fun setUpImagesList() {
        imagesAdapter = ImagesAdapter(LayoutInflater.from(this), this, this)
        imagesRv.apply {
            layoutManager = GridLayoutManager(this@AllImagesActivity, 3)
            adapter = imagesAdapter
        }
    }

    override fun addImagesClicked() {
        presenter?.addImagesClicked()
    }

    override fun imageClicked(cameraImage: CameraImage) {
        presenter?.imageClicked(cameraImage)
    }

    override fun showImages(items: ArrayList<Any>) {
        imagesAdapter?.replaceItems(items)
    }

    // Secure camera
    override fun goToSecureCamera(albumKey: String) {
        Intent(this, SecureCameraActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .run { startActivity(this) }
    }

    // Image detail
    override fun goToImageDetail(imageKey: String) {
        Intent(this, ImageDetailActivity::class.java)
                .putExtra(Constants.IMAGE_KEY, imageKey)
                .run { startActivity(this) }
    }
}
