package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.CameraImage
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
interface CameraImagesDataSource {

    fun getCameraImage(key: String): Observable<CameraImage>

    fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage>

    fun getImageCountInAlbum(albumKey: String): Observable<Int>

    fun getFirstFiveImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>>

    fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>>

    fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>>

}