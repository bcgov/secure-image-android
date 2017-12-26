package ca.bc.gov.secureimage.screens.albums

import ca.bc.gov.secureimage.data.models.Album
import ca.bc.gov.secureimage.data.repos.albums.AlbumsRepo
import ca.bc.gov.secureimage.data.repos.locationrepo.LocationRepo
import com.github.florent37.rxgps.RxGps
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class AlbumsPresenter(
        private val view: AlbumsContract.View,
        private val albumsRepo: AlbumsRepo,
        private val locationRepo: LocationRepo,
        private val rxGps: RxGps
) : AlbumsContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.setUpSettingsListener()

        view.setUpAlbumsList()

        view.setUpCreateAlbumListener()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown() {
        getLocation()

        view.showAlbumItems(ArrayList())
        getAlbums()
    }

    override fun viewHidden() {
        disposables.clear()
    }

    override fun settingsClicked() {
        view.goToSettings()
    }

    /**
     * Grabs user location and caches it
     */
    fun getLocation() {
        locationRepo.getLocation(rxGps, true)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = { view.showError(it.message ?: "Error retrieving location") },
                onSuccess = { })
                .addTo(disposables)
    }

    /**
     * Grabs all the albums with preview image from the albums repo
     * Orders albums by updated time and displays in view
     */
    fun getAlbums() {
        albumsRepo.getAllAlbums()
                .flatMapIterable { it }
                .toSortedList { album1, album2 -> album1.compareTo(album2) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving albums")
                },
                onSuccess = {
                    val items = ArrayList<Any>()
                    items.addAll(it)
                    view.showAlbumItems(items)
                })
                .addTo(disposables)
    }

    // Create album
    override fun createAlbumClicked() {
        createAlbum()
    }

    /**
     * Creates an album with default values and goes to the create album page on success
     */
    fun createAlbum() {
        albumsRepo.createAlbum()
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error creating album")
                },
                onSuccess = {
                    view.goToCreateAlbum(it.key)
                })
                .addTo(disposables)
    }

    // Album clicks
    override fun albumClicked(album: Album) {
        view.goToCreateAlbum(album.key)
    }
}