package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable
import io.realm.Realm

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object CameraImagesLocalDataSource : CameraImagesDataSource {

    override fun saveImage(cameraImage: CameraImage): Observable<CameraImage> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.copyToRealm(cameraImage)
                emitter.onNext(cameraImage)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getAllImages(): Observable<ArrayList<CameraImage>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val images = realm.copyFromRealm(realm.where(CameraImage::class.java).findAll())
                emitter.onNext(ArrayList(images))
            }
            realm.close()

            emitter.onComplete()
        }
    }
}