package ca.bc.gov.secureimage.common.adapters.images

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.Image
import kotlinx.android.synthetic.main.item_image.view.*

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class ImageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(image: Image) = with(itemView) {
        imageIv.setImageBitmap(image.getScaledBitmap())
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