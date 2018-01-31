package ca.bc.gov.mobileauthentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import ca.bc.gov.mobileauthentication.common.Constants
import ca.bc.gov.mobileauthentication.common.exceptions.TokenNotFoundException
import ca.bc.gov.mobileauthentication.common.utils.UrlUtils
import ca.bc.gov.mobileauthentication.data.AuthApi
import ca.bc.gov.mobileauthentication.data.models.Token
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRepo
import ca.bc.gov.mobileauthentication.di.Injection
import ca.bc.gov.mobileauthentication.di.InjectionUtils
import ca.bc.gov.mobileauthentication.screens.redirect.RedirectActivity
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
class MobileAuthenticationClient(
        private val context: Context,
        override val baseUrl: String,
        override val realmName: String,
        override val authEndpoint: String,
        override val redirectUri: String,
        override val clientId: String) : MobileAuthenticationContract {

    private val disposables = CompositeDisposable()

    private val gson: Gson = Injection.provideGson()
    private val grantType: String = Constants.GRANT_TYPE_AUTH_CODE
    private val authApi: AuthApi = InjectionUtils.getAuthApi(UrlUtils.cleanBaseUrl(baseUrl))
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    private val tokenRepo: TokenRepo = InjectionUtils.getTokenRepo(
            authApi, realmName, grantType, redirectUri, clientId, sharedPrefs)

    private var passedRequestCode: Int = DEFAULT_REQUEST_CODE

    override fun authenticate(requestCode: Int) {
        this.passedRequestCode = requestCode
        Intent(context, RedirectActivity::class.java)
                .putExtra(RedirectActivity.BASE_URL, baseUrl)
                .putExtra(RedirectActivity.REALM_NAME, realmName)
                .putExtra(RedirectActivity.AUTH_ENDPOINT, authEndpoint)
                .putExtra(RedirectActivity.REDIRECT_URI, redirectUri)
                .putExtra(RedirectActivity.CLIENT_ID, clientId)
                .run { (context as Activity).startActivityForResult(this, requestCode) }
    }

    override fun handleAuthResult(
            requestCode: Int, resultCode: Int, data: Intent?,
            tokenCallback: TokenCallback) {

        if (resultCode == Activity.RESULT_OK && requestCode == passedRequestCode && data != null) {
            val success = data.getBooleanExtra(MobileAuthenticationClient.SUCCESS, false)
            if (success) {
                val tokenJson = data.getStringExtra(MobileAuthenticationClient.TOKEN_JSON)
                val token: Token = gson.fromJson(tokenJson, Token::class.java)
                tokenCallback.onSuccess(token)
            }
            else {
                val errorMessage = data.getStringExtra(MobileAuthenticationClient.ERROR_MESSAGE)
                tokenCallback.onError(Throwable(errorMessage))
            }
        }
    }

    override fun getToken(tokenCallback: TokenCallback) {
        tokenRepo.getToken()
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    tokenCallback.onError(it)
                },
                onSuccess = { token ->
                    tokenCallback.onSuccess(token)
                },
                onComplete = {
                    tokenCallback.onError(TokenNotFoundException())
                }
        ).addTo(disposables)
    }

    override fun refreshToken(tokenCallback: TokenCallback) {
        tokenRepo.getToken()
                .flatMap { token -> tokenRepo.refreshToken(token) }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    tokenCallback.onError(it)
                },
                onSuccess = { token ->
                    tokenCallback.onSuccess(token)
                }
        ).addTo(disposables)
    }

    override fun deleteToken(deleteCallback: DeleteCallback) {
        tokenRepo.deleteToken()
                .ignoreElements().subscribeBy(
                onError = {
                    deleteCallback.onError(it)
                },
                onComplete = {
                    deleteCallback.onSuccess()
                }
        ).addTo(disposables)
    }

    override fun clear() {
        disposables.clear()
    }

    interface TokenCallback {
        fun onError(throwable: Throwable)
        fun onSuccess(token: Token)
    }

    interface DeleteCallback {
        fun onError(throwable: Throwable)
        fun onSuccess()
    }

    companion object {
        const val DEFAULT_REQUEST_CODE = 1012
        const val SUCCESS = "SUCCESS"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val TOKEN_JSON = "TOKEN_JSON"
    }
}