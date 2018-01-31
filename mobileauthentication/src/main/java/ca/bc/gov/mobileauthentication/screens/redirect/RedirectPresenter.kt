package ca.bc.gov.mobileauthentication.screens.redirect

import ca.bc.gov.mobileauthentication.common.utils.UrlUtils
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRepo
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class RedirectPresenter(
        private val view: RedirectContract.View,
        private val authEndpoint: String,
        private val redirectUri: String,
        private val clientId: String,
        private val responseType: String,
        private val tokenRepo: TokenRepo,
        private val gson: Gson
): RedirectContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        setViewLoginMode()
        view.setUpLoginListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun loginClicked() {
        if (!view.loading) view.loadWithChrome(buildAuthUrl())
    }

    // Auth url
    fun buildAuthUrl(): String =
            "$authEndpoint?response_type=$responseType&client_id=$clientId&redirect_uri=$redirectUri"

    // Redirect
    override fun redirectReceived(redirectUrl: String) {
        if (!redirectUrl.contains("code=".toRegex())) {
            return
        }

        val code = UrlUtils.extractCode(redirectUrl)
        getToken(code)
    }

    private fun getToken(code: String) {
        tokenRepo.getToken(code)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    setViewLoadingMode()
                }.subscribeBy(
                onError = {
                    setViewLoginMode()

                    view.setResultError(it.message ?: "Error logging in")
                    view.finish()
                },
                onSuccess = { token ->
                    setViewLoginMode()

                    val tokenJson = gson.toJson(token)
                    view.setResultSuccess(tokenJson)
                    view.finish()
                }
        ).addTo(disposables)
    }

    fun setViewLoginMode() {
        view.loading = false
        view.hideLoading()
        view.setLoginTextLogin()
    }

    fun setViewLoadingMode() {
        view.loading = true
        view.showLoading()
        view.setLoginTextLoggingIn()
    }
}