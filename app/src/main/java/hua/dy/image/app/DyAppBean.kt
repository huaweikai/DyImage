package hua.dy.image.app

/**
 * 抖音包名
 */
const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"
const val DY_IMAGE_SECOND_MENU = "dy_image"

const val DY_CACHE_PATH = "/cache/picture/fresco_cache/*"

object DyAppBean: AppBean(
    packageName = DY_PACKAGE_NAME,
    providerSecond = DY_IMAGE_SECOND_MENU,
    cachePath = listOf(
        CachePath("/cache/picture/im_fresco_cache/*", "chat"),
        CachePath(DY_CACHE_PATH, "all")
    )
)