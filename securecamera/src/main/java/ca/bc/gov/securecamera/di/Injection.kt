package ca.bc.gov.securecamera.di

import ca.bc.gov.securecamera.data.CameraImagesLocalDataSource
import ca.bc.gov.securecamera.data.CameraImagesRepo

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object Injection {

    fun provideCameraImagesRepo(): CameraImagesRepo = CameraImagesRepo.getInstance(CameraImagesLocalDataSource)

}