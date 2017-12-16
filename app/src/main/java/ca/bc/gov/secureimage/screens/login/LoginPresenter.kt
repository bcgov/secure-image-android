package ca.bc.gov.secureimage.screens.login

import ca.bc.gov.secureimage.data.repos.user.UserRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class LoginPresenter(
        private val view: LoginContract.View,
        private val userRepo: UserRepo
): LoginContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        checkForExistingUser()
    }

    override fun dispose() {
        disposables.dispose()
    }

    /**
     * Checks for existing user.
     * If the email is not blank then on success is called and user is sent to albums page
     * If the email is blank then on complete is called and the enter email ui is displayed.
     */
    fun checkForExistingUser() {
        userRepo.getUser()
                .filter { it.email.isNotBlank() }
                .firstElement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error checking for user")
                },
                onSuccess = {
                    view.goToAlbums()
                },
                onComplete = {
                    view.setContentView()
                    view.setUpLoginListener()
                }
        ).addTo(disposables)
    }

    // Login
    override fun loginClicked(email: String) {
        if(email.isBlank()) {
            view.showError("Please enter an email")
            return
        }

        val govEmail = "$email@gov.bc.ca"
        saveUser(govEmail)
    }

    /**
     * Grabs the current user, updates the fields and saves locally
     * On success goes to main albums page
     */
    fun saveUser(govEmail: String) {
        userRepo.getUser()
                .observeOn(Schedulers.io())
                .flatMap {
                    it.email = govEmail
                    userRepo.saveUser(it)
                }
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error saving user")
                },
                onSuccess = {
                    view.goToAlbums()
                }
        ).addTo(disposables)
    }

}