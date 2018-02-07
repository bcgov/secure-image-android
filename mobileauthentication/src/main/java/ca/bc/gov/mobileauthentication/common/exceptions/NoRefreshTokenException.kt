package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 * Thrown when token refresh is called and the refresh token does not exist.
 */
class NoRefreshTokenException : Throwable() {
    override val message: String?
        get() = "No refresh token"
}