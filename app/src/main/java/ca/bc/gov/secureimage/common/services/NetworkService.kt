package ca.bc.gov.secureimage.common.services

import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by Aidan Laing on 2017-12-28.
 *
 */
class NetworkService(
        private val connectivityManager: ConnectivityManager
) {

    sealed class NetworkType {
        object WifiConnection: NetworkType()
        object MobileConnection: NetworkType()
        object NoConnection: NetworkType()
    }

    fun getNetworkTypeListener(initialDelay: Long, period: Long, unit: TimeUnit): Observable<NetworkType> {
        return Observable.interval(initialDelay, period, unit)
                .map { getNetworkType() }
    }

    fun getNetworkType(): NetworkType {
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        val isConnected = activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
        val isWiFi = activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_WIFI
        val isMobile = activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_MOBILE

        return when {
            isConnected && isWiFi -> NetworkType.WifiConnection
            isConnected && isMobile -> NetworkType.MobileConnection
            else -> NetworkType.NoConnection
        }
    }

}