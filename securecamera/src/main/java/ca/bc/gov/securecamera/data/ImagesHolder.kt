package ca.bc.gov.securecamera.data

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object ImagesHolder {

    private var images = ArrayList<ByteArray>()

    fun addImage(image: ByteArray) {
        images.add(image)
    }

    fun deleteAllImages() {
        images = ArrayList()
    }

    fun getAllImages() = images

}