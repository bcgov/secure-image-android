package ca.bc.gov.mobileauthentication.common.utils

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
object UrlUtils {

    /**
     * Checks to see if base url ends with a /
     * If the url ends with a / then return the passed url
     * If the url DOES NOT end with a / then return the passed url concatenated with /
     */
    fun cleanBaseUrl(baseUrl: String): String {
        return if (!baseUrl.endsWith("/")) {
            var cleanedBaseUrl = baseUrl
            cleanedBaseUrl += "/"
            cleanedBaseUrl
        } else {
            baseUrl
        }
    }

    /**
     * Extracts code query param form url by taking a substring between
     * code= and the first & or the end of the String
     */
    fun extractCode(codeUrl: String): String {
        return codeUrl.substringAfter("code=").substringBefore("&")
    }

}