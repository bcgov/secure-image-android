package ca.bc.gov.secureimage.data.models

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
open class CameraImage : RealmObject() {

    @PrimaryKey
    var key: String = UUID.randomUUID().toString()

    @Index
    var albumKey: String = ""

    var createdTime: Long = System.currentTimeMillis()
    var updatedTime: Long = System.currentTimeMillis()

    var imageByteArray: ByteArray = ByteArray(64)
    var thumbnailArray: ByteArray = ByteArray(16)

    var lat: Double = 0.0
    var lon: Double = 0.0

    @Ignore
    var selected: Boolean = false

    fun compareTo(cameraImage: CameraImage): Int  = when {
        createdTime > cameraImage.createdTime -> 1
        createdTime < cameraImage.createdTime -> -1
        else -> 0
    }
}