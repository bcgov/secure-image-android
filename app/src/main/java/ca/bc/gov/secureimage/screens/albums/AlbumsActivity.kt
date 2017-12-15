package ca.bc.gov.secureimage.screens.albums

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.common.adapters.albums.AlbumsAdapter
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.screens.createalbum.CreateAlbumActivity
import kotlinx.android.synthetic.main.activity_albums.*

class AlbumsActivity : AppCompatActivity(), AlbumsContract.View {

    override var presenter: AlbumsContract.Presenter? = null

    private var albumsAdapter: AlbumsAdapter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_albums)

        AlbumsPresenter(
                this,
                Injection.provideAlbumsRepo()
        )

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

    // Album list
    override fun setUpAlbumsList() {
        albumsAdapter = AlbumsAdapter(LayoutInflater.from(this))
        albumsRv.apply {
            layoutManager = LinearLayoutManager(this@AlbumsActivity)
            adapter = albumsAdapter
        }
    }

    override fun showAlbumItems(items: ArrayList<Any>) {
        albumsAdapter?.replaceItems(items)
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
}
