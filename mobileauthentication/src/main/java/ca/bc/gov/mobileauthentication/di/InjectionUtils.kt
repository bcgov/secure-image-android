package ca.bc.gov.mobileauthentication.di

import android.content.SharedPreferences
import ca.bc.gov.mobileauthentication.data.AuthApi
import ca.bc.gov.mobileauthentication.common.Constants
import ca.bc.gov.mobileauthentication.data.repos.token.SecureSharedPrefs
import ca.bc.gov.mobileauthentication.data.repos.token.TokenLocalDataSource
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRemoteDataSource
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRepo
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.security.KeyStore

/**
 * Created by Aidan Laing on 2018-01-18.
 *
 */
object InjectionUtils {

    /**
     * Gets Auth Api with standard params
     */
    fun getAuthApi(
            apiDomain: String,
            gson: Gson = Injection.provideGson(),
            converterFactory: Converter.Factory = Injection.provideConverterFactory(gson),
            callAdapterFactory : CallAdapter.Factory = Injection.provideCallAdapterFactory(),
            readTimeOut: Long = Constants.READ_TIME_OUT,
            connectTimeOut: Long = Constants.CONNECT_TIME_OUT,
            loggingLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BODY,
            httpLoggingInterceptor: HttpLoggingInterceptor = Injection.provideHttpLoggingInterceptor(
                    loggingLevel),
            okHttpClient: OkHttpClient = Injection.provideOkHttpClient(
                    readTimeOut, connectTimeOut, httpLoggingInterceptor),
            retrofit: Retrofit = Injection.provideRetrofit(
                    apiDomain, okHttpClient, converterFactory, callAdapterFactory)
    ): AuthApi = Injection.provideAuthApi(retrofit)

    /**
     * Gets Token Repo with standard params
     */
    fun getTokenRepo(
            authApi: AuthApi,
            realmName: String,
            grantType: String,
            redirectUri: String,
            clientId: String,
            sharedPreferences: SharedPreferences,
            gson: Gson = Injection.provideGson(),
            keyStore: KeyStore = Injection.provideKeyStore(),
            secureSharedPrefs: SecureSharedPrefs = Injection.provideSecureSharedPrefs(keyStore, sharedPreferences)
    ): TokenRepo = TokenRepo.getInstance(
            TokenRemoteDataSource.getInstance(authApi, realmName, grantType, redirectUri, clientId),
            TokenLocalDataSource.getInstance(gson, secureSharedPrefs)
    )

}