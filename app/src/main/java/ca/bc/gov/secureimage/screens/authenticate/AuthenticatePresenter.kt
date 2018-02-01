package ca.bc.gov.secureimage.screens.authenticate

/**
 * Created by Aidan Laing on 2018-01-31.
 *
 */
class AuthenticatePresenter(
        private val view: AuthenticateContract.View,
        private val isDeviceSecure: Boolean,
        private val isKeyguardSecure: Boolean
) : AuthenticateContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {
        if (!isDeviceSecure || !isKeyguardSecure) {
            view.showNotSecureErrorMessage()
            view.finish()

        } else {
            view.hideLogo()
            view.hideInfo()
            view.hideAuthenticate()
            view.goToConfirmDeviceCredentials()
        }
    }

    override fun dispose() {

    }

    // Authenticate
    override fun authenticateClicked() {
        view.goToConfirmDeviceCredentials()
    }

    // Result
    override fun onResult(isResultOk: Boolean, isCredentialsRequestCode: Boolean) {
        if (isResultOk && isCredentialsRequestCode) {
            view.goToAlbums()
            view.finish()

        } else {
            view.showLogo()
            view.showInfo()
            view.showAuthenticate()
            view.setUpAuthenticateListener()
        }
    }
}