@file:Suppress("unused")
package hua.dy.image.utils

import android.content.res.Resources
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

val Int.dp2Px: Int get()  {
    return (this * Resources.getSystem().displayMetrics.density).roundToInt()
}

val Float.dp2Px: Float get()  {
    return (this * Resources.getSystem().displayMetrics.density)
}

val screenHeightPx: Int get() = Resources.getSystem().displayMetrics.heightPixels

val screenWidthPx: Int get() = Resources.getSystem().displayMetrics.widthPixels

val screenHeight: Dp get() = Dp(
    (Resources.getSystem().displayMetrics.heightPixels / Resources.getSystem().displayMetrics.density)
)

val screenWidth: Dp get() = Dp(
    (Resources.getSystem().displayMetrics.widthPixels / Resources.getSystem().displayMetrics.density)
)