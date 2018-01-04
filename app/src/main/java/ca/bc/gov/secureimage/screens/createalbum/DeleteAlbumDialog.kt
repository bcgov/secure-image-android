package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.dialog_delete_album.*

/**
 * Created by Aidan Laing on 2017-12-15.
 *
 */
class DeleteAlbumDialog(
        context: Context?,
        private val deleteListener: DeleteListener
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_delete_album)

        cancelTv.setOnClickListener {
            hide()
        }

        deleteTv.setOnClickListener {
            deleteListener.deleteConfirmed()
            hide()
        }
    }

    interface DeleteListener {
        fun deleteConfirmed()
    }
}