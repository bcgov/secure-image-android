package ca.bc.gov.mobileauthentication.data.repos.token

import ca.bc.gov.mobileauthentication.common.exceptions.RefreshExpiredException
import ca.bc.gov.mobileauthentication.data.models.Token
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
class TokenRepo
private constructor(
        private val remoteDataSource: TokenDataSource,
        private val localDataSource: TokenDataSource
) : TokenDataSource {

    companion object {

        @Volatile
        private var INSTANCE: TokenRepo? = null

        fun getInstance(
                remoteDataSource: TokenDataSource,
                localDataSource: TokenDataSource
        ): TokenRepo = INSTANCE ?: synchronized(this) {
            INSTANCE ?: TokenRepo(remoteDataSource, localDataSource)
                    .also { INSTANCE = it }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    /**
     * Gets token from remote if code IS NOT null
     * Gets token from local if code IS null
     * Returns token if valid
     * If token from local db refresh is expired then @see ca.bc.gov.mobileauthentication.common.exceptions.RefreshExpiredException will be thrown.
     * If refresh token is not expired and token is expired then token will be refreshed and returned
     */
    override fun getToken(code: String?): Observable<Token> {
        return if (code != null) {
            remoteDataSource.getToken(code)
                    .flatMap { localDataSource.saveToken(it) }
        } else {
            localDataSource.getToken()
                    .flatMap {
                        when {
                            it.isRefreshExpired() -> Observable.error(RefreshExpiredException())
                            it.isExpired() -> refreshToken(it)
                            else -> Observable.just(it)
                        }
                    }
        }
    }

    /**
     * Saves token locally
     */
    override fun saveToken(token: Token): Observable<Token> {
        return localDataSource.saveToken(token)
    }

    /**
     * Refreshes token and saves to local db
     * If passed token refresh token is expired then @see ca.bc.gov.mobileauthentication.common.exceptions.RefreshExpiredException will be thrown.
     */
    override fun refreshToken(token: Token): Observable<Token> {
        return if (token.isRefreshExpired()) Observable.error(RefreshExpiredException())
        else remoteDataSource.refreshToken(token)
                .flatMap { localDataSource.saveToken(it) }
    }

    /**
     * Deletes token from local db
     */
    override fun deleteToken(): Observable<Boolean> {
        return localDataSource.deleteToken()
    }
}