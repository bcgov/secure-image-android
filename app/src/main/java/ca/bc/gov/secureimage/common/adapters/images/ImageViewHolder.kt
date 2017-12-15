package ca.bc.gov.secureimage.common.adapters.images

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.CameraImage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_image.view.*

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class ImageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(cameraImage: CameraImage) = with(itemView) {
        Glide.with(context)
                .load(cameraImage.byteArray)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageIv)
    }

    companion object {
        fun create(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean = false
        ): ImageViewHolder =
                ImageViewHolder(inflater.inflate(R.layout.item_image, root, attachToRoot))
    }

}