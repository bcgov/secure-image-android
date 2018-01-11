package ca.bc.gov.secureimage.data.models.remote

import com.google.gson.annotations.SerializedName

/**
 * Created by Aidan Laing on 2018-01-10.
 *
 */
data class DownloadUrlResponse(@SerializedName("url") val downloadUrl: String) {

    fun getCleanedDownloadUrl() = downloadUrl.replace("http:/", "http://")

}