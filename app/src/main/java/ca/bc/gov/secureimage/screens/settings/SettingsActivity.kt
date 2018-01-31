package ca.bc.gov.secureimage.screens.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.di.Injection
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity(), SettingsContract.View {

    override var presenter: SettingsContract.Presenter? = null

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        SettingsPresenter(
                this,
                Injection.provideUserRepo()
        )

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

    // Logout
    override fun setUpLogoutListener() {
        logoutTv.setOnClickListener {
            presenter?.logoutClicked()
        }
    }
}
