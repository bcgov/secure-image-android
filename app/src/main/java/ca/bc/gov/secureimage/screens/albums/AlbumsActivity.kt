package ca.bc.gov.secureimage.screens.albums

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.adapters.albums.AlbumViewHolder
import ca.bc.gov.secureimage.common.adapters.albums.AlbumsAdapter
import ca.bc.gov.secureimage.data.models.Album
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.screens.createalbum.CreateAlbumActivity
import ca.bc.gov.secureimage.screens.settings.SettingsActivity
import com.github.florent37.rxgps.RxGps
import kotlinx.android.synthetic.main.activity_albums.*

class AlbumsActivity : AppCompatActivity(), AlbumsContract.View, AlbumViewHolder.ClickListener {

    override var presenter: AlbumsContract.Presenter? = null

    private var albumsAdapter: AlbumsAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)

        AlbumsPresenter(
                this,
                Injection.provideAlbumsRepo(),
                Injection.provideLocationRepo(),
                RxGps(this)
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Loading
    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    // Settings
    override fun setUpSettingsListener() {
        settingsIv.setOnClickListener {
            presenter?.settingsClicked()
        }
    }

    override fun goToSettings() {
        Intent(this, SettingsActivity::class.java)
                .run { startActivity(this) }
    }

    // Album list
    override fun setUpAlbumsList() {
        albumsAdapter = AlbumsAdapter(LayoutInflater.from(this), this)
        albumsRv.apply {
            layoutManager = LinearLayoutManager(this@AlbumsActivity)
            adapter = albumsAdapter
        }
    }

    override fun showAlbumItems(items: ArrayList<Any>) {
        albumsAdapter?.replaceItems(items)
    }

    override fun albumClicked(album: Album) {
        presenter?.albumClicked(album)
    }

    // Create album
    override fun setUpCreateAlbumListener() {
        createAlbumTv.setOnClickListener {
            presenter?.createAlbumClicked()
        }
    }

    override fun goToCreateAlbum(albumKey: String) {
        Intent(this, CreateAlbumActivity::class.java)
                .putExtra(Constants.ALBUM_KEY, albumKey)
                .run { startActivity(this) }
    }

    // Onboarding
    override fun showOnboarding() {
        onboardingTitleTv.visibility = View.VISIBLE
        onboardingInfoTv.visibility = View.VISIBLE
    }

    override fun hideOnboarding() {
        onboardingTitleTv.visibility = View.GONE
        onboardingInfoTv.visibility = View.GONE
    }
}
