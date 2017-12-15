package ca.bc.gov.secureimage.screens.createalbum

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.screens.securecamera.SecureCameraActivity
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.adapters.images.AddImagesViewHolder
import ca.bc.gov.secureimage.common.adapters.images.ImagesAdapter
import ca.bc.gov.secureimage.common.ui.NoScrollGridLayoutManager
import ca.bc.gov.secureimage.screens.allimages.AllImagesActivity
import kotlinx.android.synthetic.main.activity_create_album.*

class CreateAlbumActivity : AppCompatActivity(), CreateAlbumContract.View, AddImagesViewHolder.Listener {

    override var presenter: CreateAlbumContract.Presenter? = null

    private var imagesAdapter: ImagesAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)

        // Extras
        val albumKey = intent.getStringExtra(Constants.ALBUM_KEY)
        if(albumKey == null) {
            showError("No Album key passed")
            finish()
            return
        }

        CreateAlbumPresenter(
                this,
                albumKey,
                Injection.provideAlbumsRepo()
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

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Back
    override fun setUpBackListener() {
        backIv.setOnClickListener {
            presenter?.backClicked()
        }
    }

    // Image list
    override fun setUpImagesList() {
        imagesAdapter = ImagesAdapter(LayoutInflater.from(this), this)
        imagesRv.apply {
            layoutManager = NoScrollGridLayoutManager(this@CreateAlbumActivity, 3)
            adapter = imagesAdapter
        }
    }

    override fun addImagesClicked() {
        presenter?.addImagesClicked()
    }

    override fun showImages(items: ArrayList<Any>) {
        imagesAdapter?.replaceItems(items)
    }

    // View all images
    override fun setUpViewAllImagesListener() {
        viewAllImagesTv.setOnClickListener {
            presenter?.viewAllImagesClicked()
        }
    }

    override fun showViewAllImages() {
        viewAllImagesTv.visibility = View.VISIBLE
    }

    override fun hideViewAllImages() {
        viewAllImagesTv.visibility = View.GONE
    }

    override fun goToAllImages(albumKey: String) {
        Intent(this, AllImagesActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .run { startActivity(this) }
    }

    // Upload
    override fun setUpSaveListener() {
        saveTv.setOnClickListener {
            presenter?.saveClicked(albumNameEt.text.toString())
        }
    }

    // Camera
    override fun goToSecureCamera(albumKey: String) {
        Intent(this, SecureCameraActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .run { startActivity(this) }
    }
}
