package ca.bc.gov.secureimage.data.repos.albums

import ca.bc.gov.secureimage.data.models.Album
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
class AlbumsRepo(
        private val localDataSource: AlbumsDataSource
) : AlbumsDataSource {

    companion object {

        @Volatile private var INSTANCE: AlbumsRepo? = null

        fun getInstance(localDataSource: AlbumsDataSource): AlbumsRepo =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: AlbumsRepo(localDataSource).also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun saveAlbum(album: Album): Observable<Album> {
        return localDataSource.saveAlbum(album)
    }

    override fun getAllAlbums(): Observable<ArrayList<Album>> {
        return localDataSource.getAllAlbums()
    }
}