package ca.bc.gov.secureimage.screens.authenticate

import ca.bc.gov.secureimage.RxImmediateSchedulerRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

import org.junit.Before
import org.junit.ClassRule

/**
 * Created by Aidan Laing on 2018-02-05.
 *
 */
class AuthenticatePresenterTest {

    companion object {
        @ClassRule
        @JvmField
        val rxSchedulers = RxImmediateSchedulerRule()
    }

    private lateinit var view: AuthenticateContract.View

    private lateinit var presenter: AuthenticatePresenter

    @Before
    fun setUp() {
        view = mock()

        presenter = AuthenticatePresenter(view, true, true)
    }

    @Test
    fun presenterSet() {
        verify(view).presenter = presenter
    }

    @Test
    fun subscribeSecureDeviceSecureKeyguard() {
        presenter = AuthenticatePresenter(view, true, true)
        presenter.subscribe()

        verify(view).hideLogo()
        verify(view).hideInfo()
        verify(view).hideAuthenticate()
        verify(view).goToConfirmDeviceCredentials()
    }

    @Test
    fun subscribeInsecureDeviceInsecureKeyguard() {
        presenter = AuthenticatePresenter(view, false, false)
        presenter.subscribe()

        verify(view).showNotSecureErrorMessage()
        verify(view).finish()
    }

    @Test
    fun subscribeSecureDeviceInsecureKeyguard() {
        presenter = AuthenticatePresenter(view, true, false)
        presenter.subscribe()

        verify(view).showNotSecureErrorMessage()
        verify(view).finish()
    }

    @Test
    fun subscribeInsecureDeviceSecureKeyguard() {
        presenter = AuthenticatePresenter(view, false, true)
        presenter.subscribe()

        verify(view).showNotSecureErrorMessage()
        verify(view).finish()
    }

    @Test
    fun authenticateClicked() {
        presenter.authenticateClicked()

        verify(view).goToConfirmDeviceCredentials()
    }

    @Test
    fun onResultOkAndIsCredentials() {
        presenter.onResult(true, true)

        verify(view).goToAlbums()
        verify(view).finish()
    }

    @Test
    fun onResultNotOkAndIsNotCredentials() {
        presenter.onResult(false, false)

        verify(view).showLogo()
        verify(view).showInfo()
        verify(view).showAuthenticate()
        verify(view).setUpAuthenticateListener()
    }

    @Test
    fun onResultOkAndIsNotCredentials() {
        presenter.onResult(true, false)

        verify(view).showLogo()
        verify(view).showInfo()
        verify(view).showAuthenticate()
        verify(view).setUpAuthenticateListener()
    }

    @Test
    fun onResultNotOkAndIsCredentials() {
        presenter.onResult(false, true)

        verify(view).showLogo()
        verify(view).showInfo()
        verify(view).showAuthenticate()
        verify(view).setUpAuthenticateListener()
    }

}