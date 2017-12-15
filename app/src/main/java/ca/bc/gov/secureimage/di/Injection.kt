package ca.bc.gov.secureimage.di

import ca.bc.gov.secureimage.data.repos.albums.AlbumsLocalDataSource
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesLocalDataSource
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.user.UserLocalDataSource
import ca.bc.gov.secureimage.data.repos.user.UserRepo

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object Injection {

    fun provideAlbumsRepo(): AlbumsRepo =
            AlbumsRepo.getInstance(AlbumsLocalDataSource)

    fun provideCameraImagesRepo(): CameraImagesRepo =
            CameraImagesRepo.getInstance(CameraImagesLocalDataSource)

    fun provideUserRepo(): UserRepo =
            UserRepo.getInstance(UserLocalDataSource)

}