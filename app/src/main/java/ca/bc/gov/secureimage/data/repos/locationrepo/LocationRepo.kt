package ca.bc.gov.secureimage.data.repos.locationrepo

import ca.bc.gov.secureimage.data.models.Location
import com.github.florent37.rxgps.RxGps
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-17.
 *
 */
class LocationRepo
private constructor(
        private val remoteDataSource: LocationDataSource
) : LocationDataSource {

    companion object {

        @Volatile private var INSTANCE: LocationRepo? = null

        fun getInstance(remoteDataSource: LocationDataSource): LocationRepo =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: LocationRepo(remoteDataSource).also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    private var cachedLocation: Location? = null

    private fun cacheLatLon(location: Location) {
        cachedLocation = location
    }

    override fun getLocation(rxGps: RxGps, cache: Boolean): Observable<Location> {
        return remoteDataSource.getLocation(rxGps, cache)
                .doAfterNext { if (cache) cacheLatLon(it) }
    }

    fun getCachedLocation(): Observable<Location> {
        return if(cachedLocation != null) Observable.just(cachedLocation)
        else Observable.empty()
    }

}