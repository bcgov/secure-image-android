package ca.bc.gov.secureimage.screens.createalbum

import android.content.Context
import android.support.v7.widget.GridLayoutManager

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class NoScrollGridLayoutManager(context: Context?, spanCount: Int) : GridLayoutManager(context, spanCount) {
    override fun canScrollVertically(): Boolean = false
    override fun canScrollHorizontally(): Boolean = false
}