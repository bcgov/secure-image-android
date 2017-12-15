package ca.bc.gov.secureimage.data.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
open class CameraImage : RealmObject() {

    @PrimaryKey
    var key: String = UUID.randomUUID().toString()

    var createdTime: Long = System.currentTimeMillis()
    var updatedTime: Long = System.currentTimeMillis()
    var byteArray: ByteArray = ByteArray(64)

    // Bitmap
    @Ignore
    private var cachedScaledBitmap: WeakReference<Bitmap>? = null

    fun getOriginalBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun getScaledBitmap(): Bitmap {
        return cachedScaledBitmap?.get() ?: createScaledBitmap()
    }

    fun createScaledBitmap(): Bitmap {
        val maxSize = 360
        val bitmap = getOriginalBitmap()
        val outWidth: Int
        val outHeight: Int
        val inWidth = bitmap.getWidth()
        val inHeight = bitmap.getHeight()

        if (inWidth > inHeight) {
            outWidth = maxSize
            outHeight = inHeight * maxSize / inWidth
        } else {
            outHeight = maxSize
            outWidth = inWidth * maxSize / inHeight
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false)
                .also { cachedScaledBitmap = WeakReference(it) }

        return scaledBitmap
    }

    fun compareTo(cameraImage: CameraImage): Int  = when {
        createdTime < cameraImage.createdTime -> 1
        createdTime > cameraImage.createdTime -> -1
        else -> 0
    }
}