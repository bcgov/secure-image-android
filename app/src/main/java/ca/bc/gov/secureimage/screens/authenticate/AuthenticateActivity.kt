package ca.bc.gov.secureimage.screens.authenticate

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.screens.albums.AlbumsActivity

class AuthenticateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticate)

        val keyguardManager: KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val credentialsIntent = keyguardManager.createConfirmDeviceCredentialIntent("", "")
        startActivityForResult(credentialsIntent, 403)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 403) {
            val goToAlbumsIntent = Intent(this, AlbumsActivity::class.java)
            startActivity(goToAlbumsIntent)
            finish()
        }
    }
}
