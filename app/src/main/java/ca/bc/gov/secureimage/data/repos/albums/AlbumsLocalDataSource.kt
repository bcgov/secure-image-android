package ca.bc.gov.secureimage.data.repos.albums

import ca.bc.gov.secureimage.data.models.Album
import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable
import io.realm.Realm
import io.realm.Sort

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object AlbumsLocalDataSource : AlbumsDataSource {

    override fun createAlbum(): Observable<Album> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val album = realm.copyFromRealm(realm.copyToRealm(Album()))
                emitter.onNext(album)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getAlbum(key: String): Observable<Album> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val album = realm.where(Album::class.java).equalTo("key", key).findFirst()
                if(album != null) emitter.onNext(realm.copyFromRealm(album))
                else emitter.onError(Throwable("Album not found"))
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun getAllAlbums(): Observable<ArrayList<Album>> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val albums = realm.copyFromRealm(realm.where(Album::class.java).findAll())
                for(album in albums) {
                    val images = realm.where(CameraImage::class.java)
                            .equalTo("albumKey", album.key)
                            .sort("createdTime", Sort.DESCENDING)
                            .findAll()
                    if(images.size > 0) album.previewByteArray = images[0]?.byteArray
                }
                emitter.onNext(ArrayList(albums))
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun saveAlbum(album: Album): Observable<Album> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.copyToRealmOrUpdate(album)
                emitter.onNext(album)
            }
            realm.close()

            emitter.onComplete()
        }
    }

    override fun deleteAlbum(key: String): Observable<Album> {
        return Observable.create { emitter ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                val album = realm.where(Album::class.java).equalTo("key", key).findFirst()
                album?.deleteFromRealm()

                if (album != null) emitter.onNext(album)
                else emitter.onError(Throwable("Album not found"))
            }
            realm.close()

            emitter.onComplete()
        }
    }
}