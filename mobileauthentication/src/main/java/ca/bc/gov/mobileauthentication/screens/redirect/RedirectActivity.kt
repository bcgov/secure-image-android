package ca.bc.gov.mobileauthentication.screens.redirect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import ca.bc.gov.mobileauthentication.R
import ca.bc.gov.mobileauthentication.di.InjectionUtils
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import ca.bc.gov.mobileauthentication.MobileAuthenticationClient
import ca.bc.gov.mobileauthentication.common.Constants
import ca.bc.gov.mobileauthentication.common.utils.UrlUtils
import ca.bc.gov.mobileauthentication.di.Injection
import kotlinx.android.synthetic.main.activity_login.*

class RedirectActivity : AppCompatActivity(), RedirectContract.View {

    override var presenter: RedirectContract.Presenter? = null

    override var loading: Boolean = false

    companion object {
        const val BASE_URL = "BASE_URL"
        const val REALM_NAME = "REALM_NAME"
        const val AUTH_ENDPOINT = "AUTH_ENDPOINT"
        const val REDIRECT_URI = "REDIRECT_URI"
        const val CLIENT_ID = "CLIENT_ID"
    }

    // Life cycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val baseUrl: String? = intent.getStringExtra(BASE_URL)
        val realmName: String? = intent.getStringExtra(REALM_NAME)
        val authEndpoint: String? = intent.getStringExtra(AUTH_ENDPOINT)
        val redirectUri: String? = intent.getStringExtra(REDIRECT_URI)
        val clientId: String? = intent.getStringExtra(CLIENT_ID)

        // Checking for required params
        if (baseUrl == null) {
            showToastAndFinish(getString(R.string.error_missing_base_url))
            return
        }

        if (realmName == null) {
            showToastAndFinish(getString(R.string.error_missing_realm_name))
            return
        }

        if (authEndpoint == null) {
            showToastAndFinish(getString(R.string.error_missing_auth_endpoint))
            return
        }

        if (redirectUri == null) {
            showToastAndFinish(getString(R.string.error_missing_redirect_uri))
            return
        }

        if (clientId == null) {
            showToastAndFinish(getString(R.string.error_missing_client_id))
            return
        }

        // Building presenter params
        val grantType = Constants.GRANT_TYPE_AUTH_CODE
        val responseType = Constants.RESPONSE_TYPE_CODE

        val authApi = InjectionUtils.getAuthApi(UrlUtils.cleanBaseUrl(baseUrl))
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val tokenRepo = InjectionUtils.getTokenRepo(
                authApi, realmName, grantType, redirectUri, clientId, sharedPreferences)

        val gson = Injection.provideGson()

        RedirectPresenter(
                this, authEndpoint, redirectUri, clientId, responseType, tokenRepo, gson)

        presenter?.subscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dispose()
    }

    // Toasts
    private fun showToastAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }

    // Deep link triggered
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkIntentForRedirect(intent)
    }

    private fun checkIntentForRedirect(intent: Intent?) {
        if (intent != null && intent.action == Intent.ACTION_VIEW && intent.data != null) {
            presenter?.redirectReceived(intent.data.toString())
        }
    }

    // Loading
    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    // Login
    override fun setUpLoginListener() {
        loginTv.setOnClickListener {
            presenter?.loginClicked()
        }
    }

    override fun setLoginTextLogin() {
        loginTv.setText(R.string.login)
    }

    override fun setLoginTextLoggingIn() {
        loginTv.setText(R.string.logging_in)
    }

    /**
     * Goes to Chrome custom tab
     */
    override fun loadWithChrome(url: String) {
        CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse(url))
    }

    // Results
    override fun setResultError(errorMessage: String) {
        val data = Intent()
        data.putExtra(MobileAuthenticationClient.SUCCESS, false)
        data.putExtra(MobileAuthenticationClient.ERROR_MESSAGE, errorMessage)
        setResult(Activity.RESULT_OK, data)
    }

    override fun setResultSuccess(tokenJson: String) {
        val data = Intent()
        data.putExtra(MobileAuthenticationClient.SUCCESS, true)
        data.putExtra(MobileAuthenticationClient.TOKEN_JSON, tokenJson)
        setResult(Activity.RESULT_OK, data)
    }
}
