package ca.bc.gov.secureimage.screens.settings

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
interface SettingsContract {

    interface View : BaseView<Presenter> {
        fun finish()

        fun showError(message: String)

        fun setUpBackListener()

        fun setUpLogoutListener()
    }

    interface Presenter : BasePresenter {

        fun backClicked()

        fun logoutClicked()

    }

}