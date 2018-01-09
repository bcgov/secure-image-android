package ca.bc.gov.secureimage.data

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.ResponseBody




/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
interface AppApi {

    @Multipart
    @POST("/minio/secure-image/")
    fun uploadImage(
            //@Part("name") name: RequestBody,
            @Part image: MultipartBody.Part
    ): Observable<ResponseBody>

}