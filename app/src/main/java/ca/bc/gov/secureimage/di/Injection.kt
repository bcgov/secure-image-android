package ca.bc.gov.secureimage.di

import ca.bc.gov.secureimage.data.repos.albums.AlbumsLocalDataSource
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
object Injection {

    fun provideAlbumsRepo(): AlbumsRepo =
            AlbumsRepo.getInstance(AlbumsLocalDataSource)

}