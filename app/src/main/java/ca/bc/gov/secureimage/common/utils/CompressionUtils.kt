package ca.bc.gov.secureimage.common.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


/**
 * Created by Aidan Laing on 2017-12-26.
 *
 */
object CompressionUtils {

    fun compressToJpeg(
            imageBytes: ByteArray,
            quality: Int,
            reqWidth: Int,
            reqHeight: Int): ByteArray {

        val options = BitmapFactory.Options()

        // Decode with bounds to get dimensions
        options.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        // Calculate in sample size
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode without bounds using in sample size
        options.inJustDecodeBounds = false
        val scaledBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)

        // Compress
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        return outputStream.toByteArray()
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}