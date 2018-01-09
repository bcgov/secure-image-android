package ca.bc.gov.secureimage.data.repos.albums

import ca.bc.gov.secureimage.data.models.local.Album
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
class AlbumsRepo
private constructor(private val localDataSource: AlbumsDataSource) : AlbumsDataSource {

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

    override fun createAlbum(): Observable<Album> = localDataSource.createAlbum()

    override fun getAlbum(key: String): Observable<Album> = localDataSource.getAlbum(key)

    override fun getAllAlbums(): Observable<ArrayList<Album>> = localDataSource.getAllAlbums()

    override fun saveAlbum(album: Album): Observable<Album> = localDataSource.saveAlbum(album)

    override fun deleteAlbum(key: String): Observable<Album> = localDataSource.deleteAlbum(key)
}