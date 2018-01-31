package ca.bc.gov.mobileauthentication.common.utils

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
object UrlUtils {

    fun cleanBaseUrl(baseUrl: String): String {
        return if (!baseUrl.endsWith("/")) {
            var cleanedBaseUrl = baseUrl
            cleanedBaseUrl += "/"
            cleanedBaseUrl
        } else {
            baseUrl
        }
    }

    fun extractCode(codeUrl: String): String {
        return codeUrl.substringAfter("code=").substringBefore("&")
    }

}