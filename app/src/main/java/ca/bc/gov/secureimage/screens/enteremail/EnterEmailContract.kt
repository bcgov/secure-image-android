package ca.bc.gov.secureimage.screens.enteremail

import ca.bc.gov.secureimage.common.base.BasePresenter
import ca.bc.gov.secureimage.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
interface EnterEmailContract {

    interface View: BaseView<Presenter> {
        fun setUpLoginListener()
        fun goToAlbums()
    }

    interface Presenter: BasePresenter {
        fun loginClicked()
    }

}