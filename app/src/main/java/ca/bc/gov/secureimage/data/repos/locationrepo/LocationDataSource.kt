package ca.bc.gov.secureimage.data.repos.locationrepo

import ca.bc.gov.secureimage.data.models.Location
import com.github.florent37.rxgps.RxGps
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-17.
 *
 */
interface LocationDataSource {

    fun getLocation(rxGps: RxGps, cache: Boolean): Observable<Location>

}