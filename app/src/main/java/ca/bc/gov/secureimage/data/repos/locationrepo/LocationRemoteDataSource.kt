package ca.bc.gov.secureimage.data.repos.locationrepo

import ca.bc.gov.secureimage.data.models.Location
import com.github.florent37.rxgps.RxGps
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-17.
 *
 */
object LocationRemoteDataSource : LocationDataSource {

    override fun getLocation(rxGps: RxGps, returnCacheIfExists: Boolean): Observable<Location> {
        return rxGps.locationLowPower()
                .map { Location(it.latitude, it.longitude) }
    }

}