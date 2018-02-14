package ca.bc.gov.secureimage.screens.authenticate

import ca.bc.gov.secureimage.RxImmediateSchedulerRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

import org.junit.Before
import org.junit.ClassRule

/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by Aidan Laing on 2017-12-12.
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