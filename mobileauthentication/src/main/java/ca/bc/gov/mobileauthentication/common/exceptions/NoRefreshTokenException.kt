package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 *
 */
class NoRefreshTokenException : Throwable() {
    override val message: String?
        get() = "No refresh token"
}