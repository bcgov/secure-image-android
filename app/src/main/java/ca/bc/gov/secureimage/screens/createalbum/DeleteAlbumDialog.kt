package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.dialog_delete.*

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
        setContentView(R.layout.dialog_delete)

        cancelTv.setOnClickListener {
            hide()
        }

        deleteForeverTv.setOnClickListener {
            deleteListener.deleteForeverClicked()
            hide()
        }
    }

    interface DeleteListener {
        fun deleteForeverClicked()
    }
}