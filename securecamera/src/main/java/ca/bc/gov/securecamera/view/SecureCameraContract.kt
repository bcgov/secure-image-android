package ca.bc.gov.securecamera.view

import ca.bc.gov.securecamera.common.base.BasePresenter
import ca.bc.gov.securecamera.common.base.BaseView

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
interface SecureCameraContract {

    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {

    }

}