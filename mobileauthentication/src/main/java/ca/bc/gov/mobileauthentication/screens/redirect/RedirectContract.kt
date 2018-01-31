package ca.bc.gov.mobileauthentication.screens.redirect

import ca.bc.gov.mobileauthentication.common.BasePresenter
import ca.bc.gov.mobileauthentication.common.BaseView

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface RedirectContract {

    interface View: BaseView<Presenter> {
        var loading: Boolean

        fun finish()

        fun showLoading()
        fun hideLoading()

        fun setUpLoginListener()
        fun setLoginTextLogin()
        fun setLoginTextLoggingIn()

        fun loadWithChrome(url: String)

        fun setResultError(errorMessage: String)
        fun setResultSuccess(tokenJson: String)
    }

    interface Presenter: BasePresenter {
        fun loginClicked()

        fun redirectReceived(redirectUrl: String)
    }

}