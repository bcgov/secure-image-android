package ca.bc.gov.secureimage.common.adapters.albums

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.bc.gov.secureimage.R
import ca.bc.gov.secureimage.data.models.local.Album
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
        val imageBytes = album.previewByteArray
        Glide.with(context)
                .load(imageBytes)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageIv)

        if (imageBytes != null) {
            imagePlaceholderIv.visibility = View.GONE
        } else {
            imagePlaceholderIv.visibility = View.VISIBLE
        }

        // Name
        nameTv.text = album.getDisplayName()

        // Created time
        createdTimeTv.text = album.getCreatedTimeString()

        // Updated time
        updatedTimeTv.text = album.getLastSavedTimeString()

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