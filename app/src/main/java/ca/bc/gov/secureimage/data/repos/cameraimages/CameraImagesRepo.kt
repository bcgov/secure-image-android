package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class CameraImagesRepo
private constructor(val localDataSource: CameraImagesDataSource) : CameraImagesDataSource {

    companion object {

        @Volatile private var INSTANCE: CameraImagesRepo? = null

        fun getInstance(localDataSource: CameraImagesDataSource): CameraImagesRepo =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CameraImagesRepo(localDataSource).also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getCameraImage(key: String): Observable<CameraImage> =
            localDataSource.getCameraImage(key)

    override fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage> =
            localDataSource.saveCameraImage(cameraImage)

    override fun deleteCameraImage(cameraImage: CameraImage): Observable<CameraImage> =
            localDataSource.deleteCameraImage(cameraImage)

    override fun getCameraImageCountInAlbum(albumKey: String): Observable<Int> =
            localDataSource.getCameraImageCountInAlbum(albumKey)

    override fun getCameraImagesInAlbum(albumKey: String, amount: Int): Observable<ArrayList<CameraImage>> =
            localDataSource.getCameraImagesInAlbum(albumKey, amount)

    override fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> =
            localDataSource.getAllCameraImagesInAlbum(albumKey)

    override fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> =
            localDataSource.deleteAllCameraImagesInAlbum(albumKey)
}