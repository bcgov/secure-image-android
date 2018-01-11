package ca.bc.gov.secureimage.data.repos.cameraimages

import ca.bc.gov.secureimage.data.models.local.CameraImage
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
interface CameraImagesDataSource {

    fun saveCameraImage(cameraImage: CameraImage): Observable<CameraImage>

    fun deleteCameraImage(cameraImage: CameraImage): Observable<CameraImage>

    fun getCameraImageCountInAlbum(albumKey: String): Observable<Int>

    fun getAllCameraImagesInAlbum(albumKey: String): Observable<ArrayList<CameraImage>>

    fun deleteAllCameraImagesInAlbum(albumKey: String): Observable<Boolean>

    fun uploadCameraImage(remoteAlbumId: String, cameraImage: CameraImage): Observable<CameraImage>

}