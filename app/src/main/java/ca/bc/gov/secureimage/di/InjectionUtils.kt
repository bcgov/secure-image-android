package ca.bc.gov.secureimage.di

import ca.bc.gov.secureimage.BuildConfig
import ca.bc.gov.secureimage.common.Constants
import ca.bc.gov.secureimage.data.AppApi
import ca.bc.gov.secureimage.di.Injection
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
object InjectionUtils {

    fun getAppApi(
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
            apiDomain: String = BuildConfig.API_URL,
            retrofit: Retrofit = Injection.provideRetrofit(
                    apiDomain, okHttpClient, converterFactory, callAdapterFactory)
    ): AppApi = Injection.provideAppApi(retrofit)

}