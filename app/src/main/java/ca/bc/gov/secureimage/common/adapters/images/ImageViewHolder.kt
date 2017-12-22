package ca.bc.gov.secureimage.common.adapters.images

import android.support.v4.content.ContextCompat
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

    fun bind(cameraImage: CameraImage, selectMode: Boolean, imageClickListener: ImageClickListener) = with(itemView) {

        // Image
        Glide.with(context)
                .load(cameraImage.byteArray)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageIv)

        // Select mode
        if (selectMode) {
            selectedIv.visibility = View.VISIBLE
        } else {
            selectedIv.visibility = View.GONE
        }

        if(cameraImage.selected) {
            selectedIv.setImageResource(R.drawable.ic_radio_button_checked_black_24dp)
            selectedIv.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent))
        } else {
            selectedIv.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp)
            selectedIv.setColorFilter(ContextCompat.getColor(context, R.color.lightGray))
        }

        // Clicks
        layout.setOnClickListener {
            if(selectMode) {
                imageClickListener.imageSelected(cameraImage, adapterPosition)
            } else {
                imageClickListener.imageClicked(cameraImage)
            }
        }

    }

    companion object {
        fun create(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean = false
        ): ImageViewHolder =
                ImageViewHolder(inflater.inflate(R.layout.item_image, root, attachToRoot))
    }

    interface ImageClickListener {
        fun imageClicked(cameraImage: CameraImage)
        fun imageSelected(cameraImage: CameraImage, position: Int)
    }

}