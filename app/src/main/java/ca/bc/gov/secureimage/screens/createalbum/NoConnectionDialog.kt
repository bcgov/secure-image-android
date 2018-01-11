package ca.bc.gov.secureimage.screens.createalbum

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ca.bc.gov.secureimage.R
import kotlinx.android.synthetic.main.dialog_no_connection.*

/**
 * Created by Aidan Laing on 2018-01-11.
 *
 */
class NoConnectionDialog(context: Context?) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_no_connection)

        closeTv.setOnClickListener {
            hide()
        }
    }
}