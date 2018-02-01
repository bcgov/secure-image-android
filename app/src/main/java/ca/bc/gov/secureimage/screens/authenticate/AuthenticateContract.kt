package ca.bc.gov.secureimage.screens.authenticate

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2018-01-31.
 *
 */
interface AuthenticateContract {

    interface View : BaseView<Presenter> {
        fun finish()

        fun showNotSecureErrorMessage()

        fun hideLogo()
        fun showLogo()

        fun hideInfo()
        fun showInfo()

        fun hideAuthenticate()
        fun showAuthenticate()
        fun setUpAuthenticateListener()

        fun goToConfirmDeviceCredentials()
        fun goToAlbums()
    }

    interface Presenter : BasePresenter {
        fun authenticateClicked()

        fun onResult(isResultOk: Boolean, isCredentialsRequestCode: Boolean)
    }

}