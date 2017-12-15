package ca.bc.gov.secureimage.screens.imagedetail

import ca.bc.gov.secureimage.data.repos.cameraimages.CameraImagesRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class ImageDetailPresenter(
        private val view: ImageDetailContract.View,
        private val imageKey: String,
        private val cameraImagesRepo: CameraImagesRepo
) : ImageDetailContract.Presenter {

    private val disposables = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        view.addBackListener()

        getImage()
    }

    override fun dispose() {
        disposables.dispose()
    }

    /**
     * Grabs the image associated with the passed image key
     */
    fun getImage() {
        cameraImagesRepo.getCameraImage(imageKey)
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeBy(
                onError = {
                    view.showError(it.message ?: "Error retrieving image")
                },
                onSuccess = {
                    view.showImage(it.byteArray)
                })
                .addTo(disposables)
    }

    override fun backClicked() {
        view.finish()
    }
}