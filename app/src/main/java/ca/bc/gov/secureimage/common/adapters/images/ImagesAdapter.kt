package ca.bc.gov.secureimage.common.adapters.images

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ca.bc.gov.secureimage.data.models.Image
import ca.bc.gov.secureimage.data.models.AddImages

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class ImagesAdapter(
        private val inflater: LayoutInflater,
        private val addImagesListener: AddImagesViewHolder.Listener,
        private var items: ArrayList<Any> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Image -> IMAGE
        is AddImages -> ADD_IMAGES
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            IMAGE -> ImageViewHolder.create(inflater, parent)
            ADD_IMAGES -> AddImagesViewHolder.create(inflater, parent)
            else -> null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(items[position] as Image)
            is AddImagesViewHolder -> holder.bind(addImagesListener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun replaceItems(items: ArrayList<Any>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun insertItem(index: Int, item: Any) {
        items.add(index, items)
        notifyItemInserted(index)
    }

    companion object {
        val IMAGE = 0
        val ADD_IMAGES = 1
    }
}