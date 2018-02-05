package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 * Thrown when local database is queried and no Token exists.
 */
class TokenNotFoundException : Throwable() {
    override val message: String?
        get() = "No token found. Please call authenticate before trying to retrieve a token locally."
}