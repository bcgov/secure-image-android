package ca.bc.gov.secureimage.screens.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.di.Injection
import ca.bc.gov.secureimage.screens.albums.AlbumsActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginContract.View {

    override var presenter: LoginContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoginPresenter(
                this,
                Injection.provideUserRepo()
        )

        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Content view
    override fun setContentView() {
        setContentView(R.layout.activity_login)
    }

    // Error
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Login
    override fun setUpLoginListener() {
        logInTv.setOnClickListener {
            presenter?.loginClicked(emailEt.text.toString())
        }
    }

    // Albums
    override fun goToAlbums() {
        Intent(this, AlbumsActivity::class.java)
                .run { startActivity(this) }
        finish()
    }
}