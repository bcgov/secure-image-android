package ca.bc.gov.secureimage.data.models.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
open class User : RealmObject() {

    @PrimaryKey
    var key: String = "KEY"

    var email: String = ""

}