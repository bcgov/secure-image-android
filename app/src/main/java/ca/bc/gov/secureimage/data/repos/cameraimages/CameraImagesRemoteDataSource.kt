package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.common.exceptions.InvalidOperationException
import ca.bc.gov.secureimage.data.AppApi
import ca.bc.gov.secureimage.data.models.local.CameraImage
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
class CameraImagesRemoteDataSource
private constructor(private val appApi: AppApi) : CameraImagesDataSource {

    companion object {

        @Volatile private var INSTANCE: CameraImagesRemoteDataSource? = null

        fun getInstance(appApi: AppApi): CameraImagesRemoteDataSource =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CameraImagesRemoteDataSource(appApi).also { INSTANCE = it }
                }
    }

    override fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage> =
            Observable.error(InvalidOperationException())

    override fun deleteCameraImage(cameraImage: CameraImage): Observable<CameraImage> =
            Observable.error(InvalidOperationException())

    override fun getCameraImageCountInAlbum(albumKey: String): Observable<Int> =
            Observable.error(InvalidOperationException())

    override fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>> =
            Observable.error(InvalidOperationException())

    override fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<Boolean> =
            Observable.error(InvalidOperationException())

    override fun uploadCameraImage(cameraImage: CameraImage): Observable<CameraImage> {

        // Image
        val imageRequestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"), cameraImage.imageByteArray)

        val imagePart = MultipartBody.Part.createFormData(
                "file", UUID.randomUUID().toString(), imageRequestBody)

        return appApi.uploadImage(imagePart)
                .flatMap { Observable.just(cameraImage) }
    }
}