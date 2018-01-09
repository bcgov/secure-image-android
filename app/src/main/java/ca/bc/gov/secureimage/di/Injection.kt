package ca.bc.gov.secureimage.di

import android.net.ConnectivityManager
import ca.bc.gov.secureimage.common.services.CompressionService
import ca.bc.gov.secureimage.common.services.KeyStorageService
import ca.bc.gov.secureimage.common.services.NetworkService
import ca.bc.gov.secureimage.data.repos.albums.AlbumsLocalDataSource
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesLocalDataSource
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRemoteDataSource
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import ca.bc.gov.secureimage.data.repos.user.UserLocalDataSource
import ca.bc.gov.secureimage.data.repos.user.UserRepo
import java.security.KeyStore

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object Injection {

    fun provideAlbumsRepo(): AlbumsRepo = AlbumsRepo.getInstance(AlbumsLocalDataSource)

    fun provideCameraImagesRepo(): CameraImagesRepo =
            CameraImagesRepo.getInstance(CameraImagesLocalDataSource)

    fun provideUserRepo(): UserRepo = UserRepo.getInstance(UserLocalDataSource)

    fun provideLocationRepo(): LocationRepo = LocationRepo.getInstance(LocationRemoteDataSource)

    fun provideNetworkService(connectivityManager: ConnectivityManager): NetworkService =
            NetworkService(connectivityManager)

    fun provideCompressionService(): CompressionService = CompressionService()

    fun provideKeyStore(type: String): KeyStore = KeyStore.getInstance(type)

    fun provideKeyStorageService(keyStore: KeyStore): KeyStorageService =
            KeyStorageService(keyStore)

}