package ca.bc.gov.secureimage.data

import ca.bc.gov.secureimage.data.models.remote.UploadImageResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
interface AppApi {

    @Multipart
    @POST("/v1/album/123/")
    fun uploadImage(
            @Part imagePart: MultipartBody.Part
    ): Observable<UploadImageResponse>

}