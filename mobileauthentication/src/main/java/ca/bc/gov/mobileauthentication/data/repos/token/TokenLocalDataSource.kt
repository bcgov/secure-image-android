package ca.bc.gov.mobileauthentication.data.repos.token

import ca.bc.gov.mobileauthentication.common.exceptions.InvalidOperationException
import ca.bc.gov.mobileauthentication.data.models.Token
import com.google.gson.Gson
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
class TokenLocalDataSource
private constructor(
        private val gson: Gson,
        private val secureSharedPrefs: SecureSharedPrefs
) : TokenDataSource {

    companion object {

        private const val TOKEN_KEY = "TOKEN_KEY"

        @Volatile
        private var INSTANCE: TokenLocalDataSource? = null

        fun getInstance(gson: Gson, secureSharedPrefs: SecureSharedPrefs): TokenLocalDataSource =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: TokenLocalDataSource(gson, secureSharedPrefs)
                            .also { INSTANCE = it }
                }
    }

    override fun getToken(code: String?): Observable<Token> {
        return Observable.create { emitter ->
            val tokenJson = secureSharedPrefs.getString(TOKEN_KEY)
            if (tokenJson.isNotBlank()) {
                val token: Token = gson.fromJson(tokenJson, Token::class.java)
                emitter.onNext(token)
            }
            emitter.onComplete()
        }
    }

    override fun saveToken(token: Token): Observable<Token> {
        return Observable.create { emitter ->
            val tokenJson = gson.toJson(token)
            secureSharedPrefs.saveString(TOKEN_KEY, tokenJson)

            val savedTokenJson = secureSharedPrefs.getString(TOKEN_KEY)
            val savedToken: Token = gson.fromJson(savedTokenJson, Token::class.java)
            emitter.onNext(savedToken)
            emitter.onComplete()
        }
    }

    override fun refreshToken(token: Token): Observable<Token> {
        return Observable.error(InvalidOperationException())
    }

    override fun deleteToken(): Observable<Boolean> {
        return Observable.create { emitter ->
            secureSharedPrefs.deleteString(TOKEN_KEY)
            emitter.onNext(true)
            emitter.onComplete()
        }
    }
}