package ca.bc.gov.secureimage.data.models.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by Aidan Laing on 2018-01-09.
 *
 */
data class UploadImageResponse(@SerializedName("id") val remoteImageId: String)