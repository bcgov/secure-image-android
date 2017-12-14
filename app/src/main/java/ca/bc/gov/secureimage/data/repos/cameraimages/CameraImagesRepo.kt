package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
class CameraImagesRepo
private constructor(
        private val localDataSource: CameraImagesDataSource
) : CameraImagesDataSource {

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

    var imageCount = 0

    override fun saveImage(cameraImage: CameraImage): Observable<CameraImage> {
        return localDataSource.saveImage(cameraImage)
    }

    override fun getAllImages(): Observable<ArrayList<CameraImage>> {
        return localDataSource.getAllImages()
    }
}