package ca.bc.gov.secureimage.data

import ca.bc.gov.secureimage.data.models.remote.RemoteAlbumIdResponse
import ca.bc.gov.secureimage.data.models.remote.DownloadUrlResponse
import ca.bc.gov.secureimage.data.models.remote.UploadImageResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
interface AppApi {

    @POST("/v1/album/")
    fun getRemoteAlbumId(): Observable<RemoteAlbumIdResponse>

    @Multipart
    @POST("/v1/album/{remoteAlbumId}/")
    fun uploadImage(
            @Path("remoteAlbumId") remoteAlbumId: String,
            @Part imagePart: MultipartBody.Part
    ): Observable<UploadImageResponse>

    @GET("/v1/album/{remoteAlbumId}/")
    fun getDownloadUrl(
            @Path("remoteAlbumId") remoteAlbumId: String
    ): Observable<DownloadUrlResponse>

}