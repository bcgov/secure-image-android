package ca.bc.gov.secureimage.screens.allimages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
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
import ca.bc.gov.secureimage.screens.imagedetail.ImageDetailActivity
import kotlinx.android.synthetic.main.activity_all_images.*

class AllImagesActivity : AppCompatActivity(), AllImagesContract.View, AddImagesViewHolder.Listener, ImageViewHolder.ImageClickListener {

    override var presenter: AllImagesContract.Presenter? = null

    private var imagesAdapter: ImagesAdapter? = null

    private var refresh = true

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
        presenter?.viewShown(refresh)
    }

    override fun onPause() {
        super.onPause()
        presenter?.viewHidden()
    }

    // Refresh
    override fun setRefresh(refresh: Boolean) {
        this.refresh = refresh
    }

    // Loading
    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    // Message
    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Toolbar color
    override fun setToolbarColorPrimary() {
        toolbarBackgroundView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    override fun setToolbarColorPrimaryLight() {
        toolbarBackgroundView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryLight))
    }

    // Back
    override fun showBack() {
        backIv.visibility = View.VISIBLE
    }

    override fun hideBack() {
        backIv.visibility = View.GONE
    }

    override fun setUpBackListener() {
        backIv.setOnClickListener {
            presenter?.backClicked()
        }
    }

    // Toolbar title
    override fun showToolbarTitle() {
        toolbarTitleTv.visibility = View.VISIBLE
    }

    override fun hideToolbarTitle() {
        toolbarTitleTv.visibility = View.GONE
    }

    // Select
    override fun showSelect() {
        selectTv.visibility = View.VISIBLE
    }

    override fun hideSelect() {
        selectTv.visibility = View.GONE
    }

    override fun setUpSelectListener() {
        selectTv.setOnClickListener {
            presenter?.selectClicked()
        }
    }

    // Close select
    override fun showSelectClose() {
        toolbarSelectCloseIv.visibility = View.VISIBLE
    }

    override fun hideSelectClose() {
        toolbarSelectCloseIv.visibility = View.GONE
    }

    override fun setUpSelectCloseListener() {
        toolbarSelectCloseIv.setOnClickListener {
            presenter?.closeSelectClicked()
        }
    }

    // Select title
    override fun showSelectTitle() {
        toolbarSelectTitleTv.visibility = View.VISIBLE
    }

    override fun hideSelectTitle() {
        toolbarSelectTitleTv.visibility = View.GONE
    }

    override fun setSelectTitle(title: String) {
        toolbarSelectTitleTv.text = title
    }

    override fun setSelectTitleSelectItems() {
        toolbarSelectTitleTv.setText(R.string.select_items)
    }

    // Select delete
    override fun showSelectDelete() {
        deleteTv.visibility = View.VISIBLE
    }

    override fun hideSelectDelete() {
        deleteTv.visibility = View.GONE
    }

    override fun setUpSelectDeleteListener() {
        deleteTv.setOnClickListener {
            presenter?.selectDeleteClicked(imagesAdapter?.getSelectedImages() ?: ArrayList())
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

    override fun imageSelected(cameraImage: CameraImage, position: Int) {
        presenter?.imageSelected(cameraImage, position, imagesAdapter?.getSelectedCount() ?: 0)
    }

    override fun imageLongClicked(cameraImage: CameraImage, position: Int) {
        presenter?.imageLongClicked(cameraImage, position, imagesAdapter?.getSelectedCount() ?: 0)
    }

    override fun showImages(items: ArrayList<Any>) {
        imagesAdapter?.replaceItems(items)
    }

    override fun setSelectMode(selectMode: Boolean) {
        imagesAdapter?.setSelectMode(selectMode)
    }

    override fun itemChanged(position: Int) {
        imagesAdapter?.notifyItemChanged(position)
    }

    override fun clearSelectedImages() {
        imagesAdapter?.clearSelectedImages()
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
