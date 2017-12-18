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

    override fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.copyToRealmOrUpdate(cameraImage)
                emitter.onNext(cameraImage)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getImageCountInAlbum(albumKey: String): Observable<Int> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val cameraImages = realm.where(CameraImage::class.java)
                        .equalTo("albumKey", albumKey).findAll()
                if(cameraImages != null) emitter.onNext(cameraImages.size)
                else emitter.onNext(0)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getFixedAmountImagesInAlbum(albumKey: String, amount: Int): Observable<ArrayList<CameraImage>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val cameraImages = realm.where(CameraImage::class.java)
                        .equalTo("albumKey", albumKey).findAll().take(amount)
                emitter.onNext(ArrayList(realm.copyFromRealm(cameraImages)))
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val cameraImages = realm.where(CameraImage::class.java)
                        .equalTo("albumKey", albumKey).findAll()
                if(cameraImages != null) emitter.onNext(ArrayList(realm.copyFromRealm(cameraImages)))
                else emitter.onNext(ArrayList())
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val cameraImages = realm.where(CameraImage::class.java)
                        .equalTo("albumKey", albumKey).findAll()
                cameraImages.deleteAllFromRealm()

                if(cameraImages != null) emitter.onNext(ArrayList(cameraImages))
                else emitter.onNext(ArrayList())
            }
            realm.close()

            emitter.onComplete()
        }
    }
}