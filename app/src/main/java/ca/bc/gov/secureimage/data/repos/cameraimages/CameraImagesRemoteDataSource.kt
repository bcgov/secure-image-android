package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.mobileauthentication.MobileAuthenticationClient
import ca.bc.gov.mobileauthentication.common.exceptions.InvalidOperationException
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
private constructor(
        private val appApi: AppApi,
        private val mobileAuthenticationClient: MobileAuthenticationClient
) : CameraImagesDataSource {

    companion object {

        @Volatile private var INSTANCE: CameraImagesRemoteDataSource? = null

        fun getInstance(
                appApi: AppApi,
                mobileAuthenticationClient: MobileAuthenticationClient
        ): CameraImagesRemoteDataSource =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: CameraImagesRemoteDataSource(
                            appApi, mobileAuthenticationClient).also { INSTANCE = it }
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

    override fun uploadCameraImage(
            remoteAlbumId: String,
            cameraImage: CameraImage
    ): Observable<CameraImage> {

        // Image
        val imageRequestBody = RequestBody.create(
                MediaType.parse("image/*"), cameraImage.imageByteArray)

        val imagePart = MultipartBody.Part.createFormData(
                "file", UUID.randomUUID().toString(), imageRequestBody)

        return mobileAuthenticationClient.getTokenAsObservable()
                .flatMap { token ->
                    val authToken = "${token.bearer} ${token.accessToken}"
                    appApi.uploadImage(authToken, remoteAlbumId, imagePart)
                }
                .flatMap { Observable.just(cameraImage) }
    }
}