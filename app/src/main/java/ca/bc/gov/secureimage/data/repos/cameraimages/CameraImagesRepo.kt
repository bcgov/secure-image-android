package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.local.CameraImage
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class CameraImagesRepo
private constructor(
        private val localDataSource: CameraImagesDataSource,
        private val remoteDataSource: CameraImagesDataSource
) : CameraImagesDataSource {

    companion object {

        @Volatile private var INSTANCE: CameraImagesRepo? = null

        fun getInstance(
                localDataSource: CameraImagesDataSource,
                remoteDataSource: CameraImagesDataSource
        ): CameraImagesRepo =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CameraImagesRepo(
                            localDataSource,
                            remoteDataSource
                    ).also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    /**
     * Pair is an album key and it's images. Only storing one at a time to preserve memory
     * Cache is replace each time a new list of camera images is loaded.
     */
    private var currentCachedAlbumCameraImages: Pair<String, ArrayList<CameraImage>>? = null

    // Operations
    override fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage> {
        return localDataSource.saveCameraImage(cameraImage)
                .doOnNext {
                    val albumKey = it.albumKey
                    if (currentCachedAlbumCameraImages?.first == albumKey) {
                        val cameraImages = currentCachedAlbumCameraImages?.second ?: ArrayList()
                        cameraImages.add(it)
                        currentCachedAlbumCameraImages = Pair(albumKey, cameraImages)
                    }
                }
    }

    override fun deleteCameraImage(cameraImage: CameraImage): Observable<CameraImage> {
        return localDataSource.deleteCameraImage(cameraImage)
                .doOnNext {
                    val albumKey = it.albumKey
                    if (currentCachedAlbumCameraImages?.first == albumKey) {
                        val cameraImages = currentCachedAlbumCameraImages?.second ?: ArrayList()
                        val removed = cameraImages.remove(it)
                        if (removed) currentCachedAlbumCameraImages = Pair(albumKey, cameraImages)
                    }
                }
    }

    override fun getCameraImageCountInAlbum(albumKey: String): Observable<Int> {

        if (currentCachedAlbumCameraImages?.first == albumKey) {
            val size = currentCachedAlbumCameraImages?.second?.size ?: 0
            return Observable.just(size)
        }

        return localDataSource.getCameraImageCountInAlbum(albumKey)
    }

    override fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> {

        if (currentCachedAlbumCameraImages?.first == albumKey) {
            val cameraImages = currentCachedAlbumCameraImages?.second ?: ArrayList()
            return Observable.just(cameraImages)
        }

        return localDataSource.getAllCameraImagesInAlbum(albumKey)
                .doOnNext {
                    currentCachedAlbumCameraImages = Pair(albumKey, it)
                }
    }

    override fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<Boolean> {
        return localDataSource.deleteAllCameraImagesInAlbum(albumKey)
                .doOnNext { removed ->
                    if (removed && currentCachedAlbumCameraImages?.first == albumKey) {
                        currentCachedAlbumCameraImages = null
                    }
                }
    }

    override fun uploadCameraImage(cameraImage: CameraImage): Observable<CameraImage> =
            remoteDataSource.uploadCameraImage(cameraImage)
}