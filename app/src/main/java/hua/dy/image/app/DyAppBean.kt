package hua.dy.image.app

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 抖音包名
 */
const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"
const val DY_IMAGE_SECOND_MENU = "dy_image"

const val DY_CACHE_PATH = "/cache/picture/fresco_cache/*"

@SuppressLint("SdCardPath")
const val DY_FILE_PATH = "/sdcard/Android/data/com.ss.android.ugc.aweme"

@Parcelize
data object DyAppBean: AppBean(
    packageName = DY_PACKAGE_NAME,
    providerSecond = DY_IMAGE_SECOND_MENU,
    cachePath = listOf(
        "/cache/picture/im_fresco_cache/*",
        DY_CACHE_PATH
    )
), Parcelable