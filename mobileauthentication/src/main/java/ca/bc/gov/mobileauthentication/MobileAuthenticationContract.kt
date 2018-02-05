package ca.bc.gov.mobileauthentication

import android.content.Intent
import ca.bc.gov.mobileauthentication.data.models.Token
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
interface MobileAuthenticationContract {

    val baseUrl: String
    val realmName: String
    val authEndpoint: String
    val redirectUri: String
    val clientId: String

    fun authenticate(requestCode: Int = MobileAuthenticationClient.DEFAULT_REQUEST_CODE)

    fun handleAuthResult(requestCode: Int, resultCode: Int, data: Intent?,
                         tokenCallback: MobileAuthenticationClient.TokenCallback)

    fun getToken(tokenCallback: MobileAuthenticationClient.TokenCallback)
    fun getTokenAsObservable(): Observable<Token>

    fun refreshToken(tokenCallback: MobileAuthenticationClient.TokenCallback)
    fun refreshTokenAsObservable(): Observable<Token>

    fun deleteToken(deleteCallback: MobileAuthenticationClient.DeleteCallback)
    fun deleteTokenAsObservable(): Observable<Boolean>

    fun clear()

}