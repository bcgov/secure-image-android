package ca.bc.gov.secureimage.screens.login

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface LoginContract {

    interface View: BaseView<Presenter> {
        fun setContentView()

        fun showError(message: String)

        fun setUpLoginListener()
        fun goToAlbums()
    }

    interface Presenter: BasePresenter {
        fun loginClicked(email: String)
    }

}