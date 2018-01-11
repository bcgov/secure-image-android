package ca.bc.gov.secureimage.di

import android.net.ConnectivityManager
import ca.bc.gov.secureimage.common.managers.CompressionManager
import ca.bc.gov.secureimage.common.managers.KeyStorageManager
import ca.bc.gov.secureimage.common.managers.NetworkManager
import ca.bc.gov.secureimage.data.AppApi
import ca.bc.gov.secureimage.data.repos.albums.AlbumsLocalDataSource
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesLocalDataSource
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRemoteDataSource
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRemoteDataSource
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import ca.bc.gov.secureimage.data.repos.user.UserLocalDataSource
import ca.bc.gov.secureimage.data.repos.user.UserRepo
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
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object Injection {

    fun provideAlbumsRepo(): AlbumsRepo = AlbumsRepo.getInstance(AlbumsLocalDataSource)

    fun provideCameraImagesRepo(appApi: AppApi): CameraImagesRepo =
            CameraImagesRepo.getInstance(
                    CameraImagesLocalDataSource,
                    CameraImagesRemoteDataSource.getInstance(appApi)
            )

    fun provideUserRepo(): UserRepo = UserRepo.getInstance(UserLocalDataSource)

    fun provideLocationRepo(): LocationRepo = LocationRepo.getInstance(LocationRemoteDataSource)

    fun provideNetworkService(connectivityManager: ConnectivityManager): NetworkManager =
            NetworkManager(connectivityManager)

    fun provideCompressionService(): CompressionManager = CompressionManager()

    fun provideKeyStore(type: String): KeyStore = KeyStore.getInstance(type)

    fun provideKeyStorageService(keyStore: KeyStore): KeyStorageManager =
            KeyStorageManager(keyStore)

    // OkHttpClient
    private var cachedOkHttpClient: OkHttpClient? = null

    @JvmStatic
    fun provideOkHttpClient(
            readTimeOut: Long,
            connectTimeOut: Long,
            interceptor: HttpLoggingInterceptor
    ): OkHttpClient = cachedOkHttpClient ?: OkHttpClient.Builder()
            .readTimeout(readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
            .also { cachedOkHttpClient = it }

    // Logging interceptor
    private var cachedHttpLoggingInterceptor: HttpLoggingInterceptor? = null

    @JvmStatic
    fun provideHttpLoggingInterceptor(
            loggingLevel: HttpLoggingInterceptor.Level
    ): HttpLoggingInterceptor = cachedHttpLoggingInterceptor ?: HttpLoggingInterceptor()
            .apply { level = loggingLevel }
            .also { cachedHttpLoggingInterceptor = it }

    // Gson
    private var cachedGson: Gson? = null

    @JvmStatic
    fun provideGson(): Gson = cachedGson ?: GsonBuilder()
            .setLenient()
            .create()
            .also { cachedGson = it }

    // Converter Factory
    private var cachedConverterFactory: Converter.Factory? = null

    @JvmStatic
    fun provideConverterFactory(gson: Gson): Converter.Factory = cachedConverterFactory ?:
            GsonConverterFactory.create(gson)
                    .also { cachedConverterFactory = it }

    // Call Adapter Factory
    private var cachedCallAdapterFactory: CallAdapter.Factory? = null

    @JvmStatic
    fun provideCallAdapterFactory(): CallAdapter.Factory = cachedCallAdapterFactory ?:
            RxJava2CallAdapterFactory.create()
                    .also { cachedCallAdapterFactory = it }

    // Retrofit
    private val cachedRetrofitMap: HashMap<String, Retrofit> = HashMap()

    @JvmStatic
    fun provideRetrofit(
            apiDomain: String,
            okHttpClient: OkHttpClient,
            converterFactory: Converter.Factory,
            callAdapterFactory: CallAdapter.Factory
    ): Retrofit = cachedRetrofitMap[apiDomain] ?: Retrofit.Builder()
            .baseUrl(apiDomain)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
            .also { cachedRetrofitMap.put(apiDomain, it) }

    // App Api
    fun provideAppApi(retrofit: Retrofit): AppApi = retrofit.create(AppApi::class.java)

}