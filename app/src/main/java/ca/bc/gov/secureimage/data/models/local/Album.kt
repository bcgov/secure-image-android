package ca.bc.gov.secureimage.data.models.local

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
    var comments: String = ""

    @Ignore
    var previewByteArray: ByteArray? = null

    fun compareTo(album: Album): Int = when {
        createdTime < album.createdTime -> 1
        createdTime > album.createdTime -> -1
        else -> 0
    }

    fun getDisplayName(): String {
        return if (name.isNotBlank()) name else "Unnamed Album"
    }

    fun getLastSavedTimeString(): String {
        return "Last saved ${TimeUtils.getReadableTime(updatedTime)}"
    }

    fun getCreatedTimeString(): String {
        return "Created ${TimeUtils.getReadableTime(createdTime)}"
    }

}