package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 * Thrown when token refresh is called and the refresh is expired.
 */
class RefreshExpiredException : Throwable() {
    override val message: String?
        get() = "Refresh token has expired. Please get new token using authenticate."
}