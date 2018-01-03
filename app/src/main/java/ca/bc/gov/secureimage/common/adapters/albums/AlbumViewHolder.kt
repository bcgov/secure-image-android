package ca.bc.gov.secureimage.common.adapters.albums

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.Album
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_album.view.*

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class AlbumViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

    fun bind(album: Album, clickListener: ClickListener) = with(itemView) {

        // Image
        Glide.with(context)
                .load(album.previewByteArray)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageIv)

        // Name
        nameTv.text = album.getDisplayName()

        // Update time
        updatedTimeTv.text = album.getLastSavedTime()

        // Clicks
        layout.setOnClickListener {
            clickListener.albumClicked(album)
        }
    }

    companion object {
        fun create(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean = false
        ): AlbumViewHolder =
                AlbumViewHolder(inflater.inflate(R.layout.item_album, root, attachToRoot))
    }

    interface ClickListener {
        fun albumClicked(album: Album)
    }

}