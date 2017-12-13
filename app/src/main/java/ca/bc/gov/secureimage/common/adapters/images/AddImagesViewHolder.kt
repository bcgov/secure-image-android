package ca.bc.gov.secureimage.common.adapters.images

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.item_add_images.view.*

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class AddImagesViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(listener: Listener) = with(itemView) {
        layout.setOnClickListener {
            listener.addImagesClicked()
        }
    }

    companion object {
        fun create(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean = false
        ): AddImagesViewHolder =
                AddImagesViewHolder(inflater.inflate(R.layout.item_add_images, root, attachToRoot))
    }

    interface Listener {
        fun addImagesClicked()
    }

}