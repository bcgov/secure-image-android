package ca.bc.gov.secureimage.data.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.lang.ref.WeakReference

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class Image(private val byteArray: ByteArray) {

    private var cachedScaledBitmap: WeakReference<Bitmap>? = null

    fun getOriginalBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun getScaledBitmap(maxSize: Int = 500): Bitmap {
        return cachedScaledBitmap?.get() ?: createScaledBitmap(maxSize)
    }

    fun createScaledBitmap(maxSize: Int): Bitmap {
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

}