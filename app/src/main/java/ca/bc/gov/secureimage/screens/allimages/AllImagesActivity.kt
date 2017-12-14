package ca.bc.gov.secureimage.screens.allimages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.widget.Toast
import ca.bc.gov.securecamera.di.Injection
import ca.bc.gov.securecamera.view.SecureCameraActivity
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.adapters.images.AddImagesViewHolder
import ca.bc.gov.secureimage.common.adapters.images.ImagesAdapter
import kotlinx.android.synthetic.main.activity_all_images.*

class AllImagesActivity : AppCompatActivity(), AllImagesContract.View, AddImagesViewHolder.Listener {

    override var presenter: AllImagesContract.Presenter? = null

    private var imagesAdapter: ImagesAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_images)

        AllImagesPresenter(this,
                Injection.provideCameraImagesRepo())

        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
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
        imagesAdapter = ImagesAdapter(LayoutInflater.from(this), this)
        imagesRv.apply {
            layoutManager = GridLayoutManager(this@AllImagesActivity, 3)
            adapter = imagesAdapter
        }
    }

    override fun addImagesClicked() {
        presenter?.addImagesClicked()
    }

    override fun showImages(items: ArrayList<Any>) {
        imagesAdapter?.replaceItems(items)
    }

    // Secure camera
    override fun goToSecureCamera() {
        Intent(this, SecureCameraActivity::class.java)
                .run { startActivity(this) }
    }
}
