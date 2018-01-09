package ca.bc.gov.secureimage.common.adapters.albums

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import ca.bc.gov.secureimage.data.models.local.Album

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class AlbumsAdapter(
        private val inflater: LayoutInflater,
        private val albumClickListener: AlbumViewHolder.ClickListener,
        private var items: ArrayList<Any> = ArrayList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is Album -> ALBUM
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        return when (viewType) {
            ALBUM -> AlbumViewHolder.create(inflater, parent)
            else -> null
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is AlbumViewHolder -> holder.bind(items[position] as Album, albumClickListener)
        }
    }

    override fun getItemCount(): Int = items.size

    fun replaceItems(items: ArrayList<Any>) {
        this.items = items
        notifyDataSetChanged()
    }

    companion object {
        val ALBUM = 0
    }

}