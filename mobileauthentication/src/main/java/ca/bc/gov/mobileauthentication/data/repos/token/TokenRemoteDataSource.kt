package ca.bc.gov.mobileauthentication.data.repos.token

import ca.bc.gov.mobileauthentication.common.exceptions.InvalidOperationException
import ca.bc.gov.mobileauthentication.common.exceptions.NoCodeException
import ca.bc.gov.mobileauthentication.common.exceptions.NoRefreshTokenException
import ca.bc.gov.mobileauthentication.data.AuthApi
import ca.bc.gov.mobileauthentication.data.models.Token
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
class TokenRemoteDataSource
private constructor(
        private val authApi: AuthApi,
        private val realmName: String,
        private val grantType: String,
        private val redirectUri: String,
        private val clientId: String
) : TokenDataSource {

    companion object {

        @Volatile
        private var INSTANCE: TokenRemoteDataSource? = null

        fun getInstance(
                authApi: AuthApi,
                realmName: String,
                grantType: String,
                redirectUri: String,
                clientId: String): TokenRemoteDataSource = INSTANCE ?: synchronized(this) {
            INSTANCE ?: TokenRemoteDataSource(
                    authApi, realmName, grantType, redirectUri, clientId).also { INSTANCE = it }
        }
    }

    /**
     * Exchanges code for token using authentication api
     * Returns error if code is null
     */
    override fun getToken(code: String?): Observable<Token> {
        if (code == null) return Observable.error(NoCodeException())
        return authApi.getToken(realmName, grantType, redirectUri, clientId, code)
    }

    /**
     * Invalid operation for remote data source
     */
    override fun saveToken(token: Token): Observable<Token> {
        return Observable.error(InvalidOperationException())
    }

    /**
     * Refreshes token using authentication api
     * Returns error if there is no refresh token
     */
    override fun refreshToken(token: Token): Observable<Token> {
        val refreshToken = token.refreshToken ?: return Observable.error(NoRefreshTokenException())
        return authApi.refreshToken(realmName, redirectUri, clientId, refreshToken)
    }
    /**
     * Invalid operation for remote data source
     */
    override fun deleteToken(): Observable<Boolean> {
        return Observable.error(InvalidOperationException())
    }
}