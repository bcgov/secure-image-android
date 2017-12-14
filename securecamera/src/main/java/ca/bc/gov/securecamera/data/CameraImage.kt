package ca.bc.gov.securecamera.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
open class CameraImage : RealmObject() {

    @PrimaryKey
    var position: Int = -1

    var byteArray: ByteArray = ByteArray(64)
}