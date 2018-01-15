package ca.bc.gov.secureimage.screens.albums

import ca.bc.gov.secureimage.RxImmediateSchedulerRule
import ca.bc.gov.secureimage.data.models.local.Album
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import com.github.florent37.rxgps.RxGps
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.ClassRule

/**
 * Created by Aidan Laing on 2018-01-15.
 *
 */
class AlbumsPresenterTest {

    companion object {
        @ClassRule
        @JvmField
        val rxSchedulers = RxImmediateSchedulerRule()
    }

    private lateinit var view: AlbumsContract.View

    private lateinit var albumsRepo: AlbumsRepo
    private lateinit var locationRepo: LocationRepo
    private lateinit var rxGps: RxGps

    private lateinit var presenter: AlbumsPresenter

    @Before
    fun setUp() {
        view = mock()

        albumsRepo = mock()
        locationRepo = mock()
        rxGps = mock()

        presenter = AlbumsPresenter(view, albumsRepo, locationRepo, rxGps)
    }

    @After
    fun tearDown() {
        AlbumsRepo.destroyInstance()
        LocationRepo.destroyInstance()
    }

    @Test
    fun subscribe() {
        presenter.subscribe()

        verify(view).hideLoading()
        verify(view).hideOnboarding()

        verify(view).setUpSettingsListener()

        verify(view).setUpAlbumsList()
        verify(view).setUpCreateAlbumListener()
    }

    @Test
    fun settingsClicked() {
        presenter.settingsClicked()

        verify(view).goToSettings()
    }

    @Test
    fun getAlbums() {
        val albums = ArrayList<Album>()
        albums.add(Album())
        val items = ArrayList<Any>(albums)

        whenever(albumsRepo.getAllAlbums()).thenReturn(Observable.just(albums))

        presenter.getAlbums()

        verify(view).showAlbumItems(ArrayList())
        verify(view).showLoading()

        verify(view).showAlbumItems(items)
        verify(view).hideLoading()
        verify(view).hideOnboarding()
    }

    @Test
    fun getAlbumsEmpty() {
        val albums = ArrayList<Album>()

        whenever(albumsRepo.getAllAlbums()).thenReturn(Observable.just(albums))

        presenter.getAlbums()

        verify(view).showAlbumItems(ArrayList())
        verify(view).showLoading()

        verify(view).hideLoading()
        verify(view).showOnboarding()
    }

    @Test
    fun createAlbum() {
        val album = Album()

        whenever(albumsRepo.createAlbum()).thenReturn(Observable.just(album))

        presenter.createAlbum()

        verify(view).goToCreateAlbum(album.key)
    }

    @Test
    fun albumClicked() {
        val album = Album()

        presenter.albumClicked(album)

        verify(view).goToCreateAlbum(album.key)
    }

}