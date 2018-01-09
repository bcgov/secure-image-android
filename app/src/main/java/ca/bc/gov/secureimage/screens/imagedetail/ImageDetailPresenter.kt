package ca.bc.gov.secureimage.screens.imagedetail

import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlin.math.roundToInt

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class ImageDetailPresenter(
        private val view: ImageDetailContract.View,
        private val albumKey: String,
        private val initialImageIndex: Int,
        private val cameraImagesRepo: CameraImagesRepo
) : ImageDetailContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.addCloseListener()

        view.setUpImagesList()
        view.addImagesListScrollChangeListener()

        getImages()
    }

    override fun dispose() {
        disposables.dispose()
    }

    // Close
    override fun closeClicked() {
        view.finish()
    }

    // Images
    fun getImages() {
        cameraImagesRepo.getAllCameraImagesInAlbum(albumKey)
                .flatMapIterable { it }
                .toSortedList { cameraImage1, cameraImage2 -> cameraImage1.compareTo(cameraImage2) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error getting all album images")
                },
                onSuccess = { images ->
                    val items = ArrayList<Any>(images)
                    view.showImages(items)
                    view.scrollImagesListTo(initialImageIndex)
                    getTitle(initialImageIndex, images.size)
                }
        ).addTo(disposables)
    }

    // Scroll change
    override fun imagesListScrollChanged(currentIndex: Int, imagesSize: Int, currentProgress: Int) {
        getProgress(currentIndex, imagesSize, currentProgress)
        getTitle(currentIndex, imagesSize)
    }

    // Title
    fun getTitle(currentIndex: Int, imagesSize: Int) {
        val title = "Image ${currentIndex + 1} of $imagesSize"
        view.setToolbarTitle(title)
    }

    // Progress
    fun getProgress(currentIndex: Int, imagesSize: Int, currentProgress: Int) {
        if (currentIndex + 1 > imagesSize || imagesSize == 0) return
        val progress = (((currentIndex + 1).toDouble() / imagesSize.toDouble()) * 100).roundToInt()
        if (currentProgress != progress) view.setViewedProgress(progress)
    }
}