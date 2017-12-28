package ca.bc.gov.secureimage.data.models

import ca.bc.gov.secureimage.common.utils.TimeUtils
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
open class Album : RealmObject() {

    @PrimaryKey
    var key: String = UUID.randomUUID().toString()

    var createdTime: Long = System.currentTimeMillis()
    var updatedTime: Long = System.currentTimeMillis()

    var name: String = ""

    @Ignore
    var previewByteArray: ByteArray? = null

    fun compareTo(album: Album): Int = when {
        updatedTime < album.updatedTime -> 1
        updatedTime > album.updatedTime -> -1
        else -> 0
    }

    fun getDisplayName(): String {
        return if (name.isNotBlank()) name else "Unnamed Album"
    }

    fun getDisplayUpdateTime(): String {
        return "Updated ${TimeUtils.getReadableTime(updatedTime)}"
    }

}