package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 *
 */
class RefreshExpiredException : Throwable() {
    override val message: String?
        get() = "Refresh token has expired. Please get new token using authenticate."
}