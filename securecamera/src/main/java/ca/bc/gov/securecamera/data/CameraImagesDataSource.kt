package ca.bc.gov.securecamera.data

import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
interface CameraImagesDataSource {

    fun saveImage(cameraImage: CameraImage): Observable<CameraImage>
    fun getAllImages(): Observable<ArrayList<CameraImage>>

}