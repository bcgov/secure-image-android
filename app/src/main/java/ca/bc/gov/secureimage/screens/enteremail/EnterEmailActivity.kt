package ca.bc.gov.secureimage.screens.enteremail

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.screens.albums.AlbumsActivity
import kotlinx.android.synthetic.main.activity_enter_email.*

class EnterEmailActivity : AppCompatActivity(), EnterEmailContract.View {

    override var presenter: EnterEmailContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_email)

        EnterEmailPresenter(this)
        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    override fun setUpLoginListener() {
        logInTv.setOnClickListener {
            presenter?.loginClicked()
        }
    }

    override fun goToAlbums() {
        Intent(this, AlbumsActivity::class.java)
                .run { startActivity(this) }
    }
}
