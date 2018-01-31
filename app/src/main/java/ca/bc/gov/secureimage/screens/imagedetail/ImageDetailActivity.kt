package ca.bc.gov.secureimage.screens.imagedetail

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.adapters.imagedetail.ImageDetailAdapter
import ca.bc.gov.secureimage.di.Injection
import kotlinx.android.synthetic.main.activity_image_detail.*
import android.support.v7.widget.LinearSnapHelper
import android.view.View
import ca.bc.gov.secureimage.di.InjectionUtils

class ImageDetailActivity : AppCompatActivity(), ImageDetailContract.View, View.OnScrollChangeListener {

    override var presenter: ImageDetailContract.Presenter? = null

    private var imageDetailLayoutManager: LinearLayoutManager? = null
    private var imageDetailAdapter: ImageDetailAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_image_detail)

        // Extras
        val albumKey = intent.getStringExtra(Constants.ALBUM_KEY)
        if (albumKey == null) {
            showError("No album key passed")
            finish()
            return
        }

        val imageIndex: Int = if (savedInstanceState?.containsKey(Constants.IMAGE_INDEX) == true) {
            savedInstanceState.getInt(Constants.IMAGE_INDEX, -1)
        } else {
            intent.getIntExtra(Constants.IMAGE_INDEX, -1)
        }

        if (imageIndex == -1) {
            showError("No image index passed")
            finish()
            return
        }

        ImageDetailPresenter(
                this,
                albumKey,
                imageIndex,
                Injection.provideCameraImagesRepo(
                        InjectionUtils.getAppApi()))

        presenter?.subscribe()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        val currImageIndex = imageDetailLayoutManager?.findFirstVisibleItemPosition() ?: 0
        outState?.putInt(Constants.IMAGE_INDEX, currImageIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Close
    override fun addCloseListener() {
        closeIv.setOnClickListener {
            presenter?.closeClicked()
        }
    }

    // Toolbar title
    override fun setToolbarTitle(title: String) {
        toolbarTitleTv.text = title
    }

    // Images list
    override fun setUpImagesList() {
        imageDetailLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageDetailAdapter = ImageDetailAdapter(LayoutInflater.from(this))
        imagesRv.apply {
            layoutManager = imageDetailLayoutManager
            adapter = imageDetailAdapter
        }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(imagesRv)
    }

    override fun addImagesListScrollChangeListener() {
        imagesRv.setOnScrollChangeListener(this)
    }

    override fun onScrollChange(p0: View?, p1: Int, p2: Int, p3: Int, p4: Int) {
        presenter?.imagesListScrollChanged(
                imageDetailLayoutManager?.findFirstVisibleItemPosition() ?: 0,
                imageDetailAdapter?.itemCount ?: 0,
                viewedProgressBar.progress)
    }

    override fun showImages(items: ArrayList<Any>) {
        imageDetailAdapter?.replaceItems(items)
    }

    override fun scrollImagesListTo(position: Int) {
        imageDetailLayoutManager?.scrollToPosition(position)
    }

    // Viewed progress
    override fun setViewedProgress(progress: Int) {
        viewedProgressBar.progress = progress
    }
}
