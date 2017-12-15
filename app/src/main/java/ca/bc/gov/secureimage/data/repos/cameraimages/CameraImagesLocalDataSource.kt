package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable
import io.realm.Realm

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
object CameraImagesLocalDataSource : CameraImagesDataSource {

    override fun getCameraImage(key: String): Observable<CameraImage> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val cameraImage = realm.where(CameraImage::class.java).equalTo("key", key).findFirst()
                if(cameraImage != null) emitter.onNext(realm.copyFromRealm(cameraImage))
                else emitter.onError(Throwable("Camera image not found"))
            }
            realm.close()

            emitter.onComplete()
        }
    }
}