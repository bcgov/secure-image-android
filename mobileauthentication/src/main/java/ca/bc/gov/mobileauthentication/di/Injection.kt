package ca.bc.gov.mobileauthentication.di

import android.content.SharedPreferences
import ca.bc.gov.mobileauthentication.data.AuthApi
import ca.bc.gov.mobileauthentication.data.repos.token.SecureSharedPrefs
import ca.bc.gov.mobileauthentication.data.repos.token.TokenLocalDataSource
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRemoteDataSource
import ca.bc.gov.mobileauthentication.data.repos.token.TokenRepo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.concurrent.TimeUnit

/**
 * Created by Aidan Laing on 2018-01-18.
 *
 */
object Injection {

    // OkHttpClient
    @JvmStatic
    fun provideOkHttpClient(
            readTimeOut: Long,
            connectTimeOut: Long,
            interceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()

    // Logging interceptor
    @JvmStatic
    fun provideHttpLoggingInterceptor(
            loggingLevel: HttpLoggingInterceptor.Level
    ): HttpLoggingInterceptor = HttpLoggingInterceptor()
            .apply { level = loggingLevel }

    // Gson
    private var cachedGson: Gson? = null

    @JvmStatic
    fun provideGson(): Gson = cachedGson ?: GsonBuilder()
            .setLenient()
            .create()
            .also { cachedGson = it }

    // Converter Factory
    @JvmStatic
    fun provideConverterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)

    // Call Adapter Factory
    private var cachedCallAdapterFactory: CallAdapter.Factory? = null

    @JvmStatic
    fun provideCallAdapterFactory(): CallAdapter.Factory = cachedCallAdapterFactory
            ?:
    RxJava2CallAdapterFactory.create()
            .also { cachedCallAdapterFactory = it }

    // Retrofit
    @JvmStatic
    fun provideRetrofit(
            apiDomain: String,
            okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory,
            callAdapterFactory: CallAdapter.Factory
    ): Retrofit = Retrofit.Builder()
            .baseUrl(apiDomain)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()

    // Auth Api
    @JvmStatic
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    // Token Repo
    @JvmStatic
    fun provideTokenRepo(
            authApi: AuthApi,
            realmName: String,
            grantType: String,
            redirectUri: String,
            clientId: String,
            gson: Gson,
            secureSharedPrefs: SecureSharedPrefs
    ): TokenRepo = TokenRepo.getInstance(
            TokenRemoteDataSource.getInstance(authApi, realmName, grantType, redirectUri, clientId),
            TokenLocalDataSource.getInstance(gson, secureSharedPrefs))

    // Secure shared prefs
    @JvmStatic
    fun provideSecureSharedPrefs(
            keyStore: KeyStore, sharedPreferences: SharedPreferences): SecureSharedPrefs =
            SecureSharedPrefs(keyStore, sharedPreferences)

    @JvmStatic
    fun provideKeyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore")
}