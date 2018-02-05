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

    override fun saveToken(token: Token): Observable<Token> {
        return localDataSource.saveToken(token)
    }

    override fun refreshToken(token: Token): Observable<Token> {
        return if (token.isRefreshExpired()) Observable.error(RefreshExpiredException())
        else remoteDataSource.refreshToken(token)
                .flatMap { localDataSource.saveToken(it) }
    }

    override fun deleteToken(): Observable<Boolean> {
        return localDataSource.deleteToken()
    }
}