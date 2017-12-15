package ca.bc.gov.secureimage.data.repos.albums

import ca.bc.gov.secureimage.data.models.Album
import io.reactivex.Observable

/**
 * Created by Aidan Laing on 2017-12-14.
 *
 */
interface AlbumsDataSource {

    fun createAlbum(): Observable<Album>

    fun getAlbum(key: String): Observable<Album>

    fun getAllAlbums(): Observable<ArrayList<Album>>

    fun saveAlbum(album: Album): Observable<Album>

    fun deleteAlbum(key: String): Observable<Album>

}