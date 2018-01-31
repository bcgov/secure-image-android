package ca.bc.gov.mobileauthentication.data.repos.token

import ca.bc.gov.mobileauthentication.data.models.Token
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
interface TokenDataSource {

    fun getToken(code: String? = null): Observable<Token>

    fun saveToken(token: Token): Observable<Token>

    fun refreshToken(token: Token): Observable<Token>

    fun deleteToken(): Observable<Boolean>

}