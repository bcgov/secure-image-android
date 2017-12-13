package ca.bc.gov.securecamera.view

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class SecureCameraPresenter(
        private val view: SecureCameraContract.View
) : SecureCameraContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {

    }

    override fun dispose() {

    }

}