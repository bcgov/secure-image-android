package ca.bc.gov.secureimage.screens.allimages

import ca.bc.gov.secureimage.data.models.AddImages
import ca.bc.gov.secureimage.data.models.local.CameraImage
import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-13.
 *
 */
class AllImagesPresenter(
        private val view: AllImagesContract.View,
        private val albumKey: String,
        private val cameraImagesRepo: CameraImagesRepo
) : AllImagesContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.hideLoading()

        view.setUpBackListener()
        view.setUpSelectListener()

        view.setUpSelectCloseListener()
        view.setUpSelectDeleteListener()

        showToolbarMode()

        view.setRefresh(true)
        view.setUpImagesList()
    }

    override fun dispose() {
        disposables.dispose()
    }

    override fun viewShown(refresh: Boolean) {
        if(refresh) {
            showToolbarMode()

            getImages()

            view.setRefresh(false)
        }
    }

    override fun viewHidden() {
        view.hideDeleteImages()
        disposables.clear()
    }

    /**
     * Wipes current images list clean
     * Gets all images in album by key
     * Adds a Add images model so recycler view can display an add image tile
     */
    fun getImages() {
        view.showImages(ArrayList())
        cameraImagesRepo.getAllCameraImagesInAlbum(albumKey)
                .flatMapIterable { it }
                .toSortedList { cameraImage1, cameraImage2 -> cameraImage1.compareTo(cameraImage2) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showLoading() }
                .doOnError { view.hideLoading() }
                .doOnSuccess { view.hideLoading() }.subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving images")
                },
                onSuccess = { images ->
                    val items = ArrayList<Any>()
                    items.add(AddImages())
                    items.addAll(images)
                    view.showImages(items)
                }
        ).addTo(disposables)
    }

    // Back
    override fun backClicked() {
        view.finish()
    }

    // Select
    override fun selectClicked() {
        showSelectMode()
    }

    // Close select
    override fun closeSelectClicked() {
        showToolbarMode()
    }

    fun showSelectMode() {
        view.setToolbarColorPrimaryLight()

        view.hideBack()
        view.hideToolbarTitle()
        view.hideSelect()

        view.showSelectClose()
        view.showSelectTitle()
        view.hideSelectDelete()

        view.setSelectTitleSelectItems()

        view.setSelectMode(true)
    }

    fun showToolbarMode() {
        view.setToolbarColorPrimary()

        view.showBack()
        view.showToolbarTitle()
        view.showSelect()

        view.hideSelectClose()
        view.hideSelectTitle()
        view.hideSelectDelete()

        view.setSelectMode(false)

        view.clearSelectedImages()
    }

    // Delete
    override fun selectDeleteClicked(cameraImages: ArrayList<CameraImage>) {
        view.showDeleteImages(cameraImages)
    }

    // Deletion confirmed
    override fun deleteConfirmed(cameraImages: ArrayList<CameraImage>) {
        showToolbarMode()
        deleteImages(cameraImages)
    }

    /**
     * Deletes all images passed through.
     * On complete gets new images.
     */
    fun deleteImages(cameraImages: ArrayList<CameraImage>) {
        view.showImages(ArrayList())
        Observable.fromIterable(cameraImages)
                .flatMap { cameraImagesRepo.deleteCameraImage(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showLoading() }
                .doOnError { view.hideLoading() }
                .doOnComplete { view.hideLoading() }.subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error deleting images")
                },
                onComplete = {
                    view.showMessage("Deleted successfully")
                    getImages()
                }
        ).addTo(disposables)
    }

    // Add images
    override fun addImagesClicked() {
        view.setRefresh(true)
        view.goToSecureCamera(albumKey)
    }

    // Image clicks
    override fun imageClicked(cameraImage: CameraImage, position: Int) {
        view.goToImageDetail(cameraImage.albumKey, position - 1)
    }

    // Image selected
    override fun imageSelected(cameraImage: CameraImage, position: Int, selectedCount: Int) {
        cameraImage.selected = !cameraImage.selected

        val newSelectedCount = if(cameraImage.selected) selectedCount + 1 else selectedCount - 1

        if(newSelectedCount <= 0) {
            view.setSelectTitleSelectItems()
            view.hideSelectDelete()
        } else {
            view.setSelectTitle("$newSelectedCount Selected")
            view.showSelectDelete()
        }

        view.itemChanged(position)
    }

    // Image long click
    override fun imageLongClicked(cameraImage: CameraImage, position: Int, selectedCount: Int) {
        showSelectMode()
        imageSelected(cameraImage, position, selectedCount)
    }
}