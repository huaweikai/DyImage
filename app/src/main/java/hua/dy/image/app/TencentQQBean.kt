package hua.dy.image.app

/**
 * 抖音包名
 */
const val QQ_PACKAGE_NAME = "com.tencent.mobileqq"
const val QQ_IMAGE_SECOND_MENU = "qq_image"

const val QQ_CACHE_PATH = "/Tencent/MobileQQ/chatpic/chatimg"

object TencentQQBean: AppBean(
    packageName = QQ_PACKAGE_NAME,
    providerSecond = QQ_IMAGE_SECOND_MENU,
    cachePath = listOf(
        CachePath("/Tencent/MobileQQ/chatpic/chatraw", "raw"),
        CachePath("/Tencent/MobileQQ/chatpic/chatimg", "img"),
        CachePath("/Tencent/MobileQQ/chatpic/chatthumb", "thumb"),
        CachePath("/Tencent/MobileQQ/chatpic/Temp", "Temp")
    )
)