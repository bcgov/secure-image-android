package ca.bc.gov.secureimage.common.exceptions

/**
 * Created by Aidan Laing on 2018-01-05.
 *
 */
class InvalidOperationException: Throwable() {

    override val message: String? get() = "Invalid operation"

}