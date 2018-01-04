package ca.bc.gov.secureimage.screens.createalbum

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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
import ca.bc.gov.secureimage.common.adapters.images.ImageViewHolder
import ca.bc.gov.secureimage.common.adapters.images.ImagesAdapter
import ca.bc.gov.secureimage.data.models.CameraImage
import ca.bc.gov.secureimage.screens.allimages.AllImagesActivity
import ca.bc.gov.secureimage.screens.imagedetail.ImageDetailActivity
import kotlinx.android.synthetic.main.activity_create_album.*

class CreateAlbumActivity : AppCompatActivity(), CreateAlbumContract.View,
        AddImagesViewHolder.Listener, ImageViewHolder.ImageClickListener,
        DeleteAlbumDialog.DeleteListener {

    override var presenter: CreateAlbumContract.Presenter? = null

    private var imagesAdapter: ImagesAdapter? = null

    private var deleteAlbumDialog: DeleteAlbumDialog? = null

    private var refresh = true

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

        val networkService = Injection.provideNetworkService(
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)

        CreateAlbumPresenter(
                this,
                albumKey,
                Injection.provideAlbumsRepo(),
                Injection.provideCameraImagesRepo(),
                networkService
        )

        presenter?.subscribe()
    }

    override fun onResume() {
        super.onResume()
        presenter?.viewShown(refresh)
    }

    override fun onPause() {
        super.onPause()
        val albumName = albumNameEt.text.toString()
        presenter?.viewHidden(albumName)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    override fun setRefresh(refresh: Boolean) {
        this.refresh = refresh
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

    // Network type
    override fun showNetworkType() {
        networkTypeTv.visibility = View.VISIBLE
    }

    override fun hideNetworkType() {
        networkTypeTv.visibility = View.GONE
    }

    override fun setNetworkTypeText(text: String) {
        networkTypeTv.text = text
    }

    // Image list
    override fun setUpImagesList() {
        imagesAdapter = ImagesAdapter(LayoutInflater.from(this), this, this)
        imagesRv.apply {
            layoutManager = NoScrollGridLayoutManager(this@CreateAlbumActivity, 3)
            adapter = imagesAdapter
        }
    }

    override fun addImagesClicked() {
        presenter?.addImagesClicked()
    }

    override fun imageClicked(cameraImage: CameraImage, position: Int) {
        presenter?.imageClicked(cameraImage, position)
    }

    override fun imageSelected(cameraImage: CameraImage, position: Int) {

    }

    override fun imageLongClicked(cameraImage: CameraImage, position: Int) {

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

    // Delete
    override fun setUpDeleteListener() {
        deleteIv.setOnClickListener {
            presenter?.deleteClicked()
        }
    }

    override fun showDeleteAlbumDialog() {
        if (deleteAlbumDialog == null) deleteAlbumDialog = DeleteAlbumDialog(this, this)
        deleteAlbumDialog?.show()
    }

    override fun hideDeleteAlbumDialog() {
        deleteAlbumDialog?.hide()
    }

    override fun deleteConfirmed() {
        presenter?.deleteConfirmed()
    }

    // Album name
    override fun setAlbumName(albumName: String) {
        albumNameEt.setText(albumName)
    }

    // Camera
    override fun goToSecureCamera(albumKey: String) {
        Intent(this, SecureCameraActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .run { startActivity(this) }
    }

    // Image detail
    override fun goToImageDetail(albumKey: String, imageIndex: Int) {
        Intent(this, ImageDetailActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .putExtra(Constants.IMAGE_INDEX, imageIndex)
                .run { startActivity(this) }
    }
}
