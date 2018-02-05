package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-31.
 * Thrown when there is no code to exchange for a token remotely.
 */
class NoCodeException : Throwable() {
    override val message: String?
        get() = "Code is required for getting token"
}