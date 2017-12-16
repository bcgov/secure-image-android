package ca.bc.gov.secureimage.screens.settings

import ca.bc.gov.secureimage.data.repos.user.UserRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class SettingsPresenter(
        private val view : SettingsContract.View,
        private val userRepo: UserRepo
) : SettingsContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpBackListener()

        view.setUpLogoutListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun backClicked() {
        view.finish()
    }

    override fun logoutClicked() {
        logout()
    }

    /**
     * Deletes the current user data.
     * OnSuccess user goes back to the login page.
     */
    fun logout() {
        userRepo.deleteUser()
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error logging out")
                },
                onSuccess = {
                    view.goToLogin()
                }
        ).addTo(disposables)
    }
}