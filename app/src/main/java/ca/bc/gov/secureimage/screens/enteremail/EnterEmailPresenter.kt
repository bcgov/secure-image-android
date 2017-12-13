package ca.bc.gov.secureimage.screens.enteremail

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class EnterEmailPresenter(
        private val view: EnterEmailContract.View
): EnterEmailContract.Presenter {

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpLoginListener()
    }

    override fun dispose() {

    }

    override fun loginClicked() {
        view.goToAlbums()
    }

}