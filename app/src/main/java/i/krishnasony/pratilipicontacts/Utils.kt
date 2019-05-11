package i.krishnasony.pratilipicontacts

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat

object Utils {
    fun getMaterialColor(context: Context) : ColorStateList? {
        val colors = arrayListOf(
            R.color.md_blue_grey_400,
            R.color.md_indigo_400,
            R.color.md_deep_purple_400,
            R.color.md_blue_400,
            R.color.md_orange_400,
            R.color.md_purple_400,
            R.color.md_red_400
        )
        return ContextCompat.getColorStateList(context,colors[(Math.random() * 6).toInt()])
    }
}