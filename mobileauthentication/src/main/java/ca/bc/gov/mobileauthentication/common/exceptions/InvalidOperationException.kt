package ca.bc.gov.mobileauthentication.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-05.
 * Thrown when an incorrect operation is called on a data source.
 */
class InvalidOperationException: Throwable() {

    override val message: String? get() = "Invalid operation"

}