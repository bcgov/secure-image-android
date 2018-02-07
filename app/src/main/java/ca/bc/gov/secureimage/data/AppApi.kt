package ca.bc.gov.secureimage.data

import ca.bc.gov.secureimage.data.models.remote.CreateRemoteAlbumIdResponse
import ca.bc.gov.secureimage.data.models.remote.BuildDownloadUrlResponse
import ca.bc.gov.secureimage.data.models.remote.UploadImageResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
interface AppApi {

    // Create remote album
    @POST("/v1/album/")
    fun createRemoteAlbumId(): Observable<CreateRemoteAlbumIdResponse>

    // Upload image
    @Multipart
    @POST("/v1/album/{remoteAlbumId}/")
    fun uploadImage(
            @Path("remoteAlbumId") remoteAlbumId: String,
            @Part imagePart: MultipartBody.Part
    ): Observable<UploadImageResponse>

    // Download url
    @GET("/v1/album/{remoteAlbumId}/")
    fun buildDownloadUrl(
            @Path("remoteAlbumId") remoteAlbumId: String,
            @Query("name") name: String? = null
    ): Observable<BuildDownloadUrlResponse>

}