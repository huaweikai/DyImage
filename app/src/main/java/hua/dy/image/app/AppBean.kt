package hua.dy.image.app

import android.net.Uri
import hua.dy.image.utils.ANDROID_SAF_PATH
import hua.dy.image.utils.APP_SHARED_PROVIDER_TOP_PATH
import splitties.init.appCtx
import java.io.File

/**
 * @param packageName 软件包名
 * @param providerSecond 软件存在app的Provider对应目录
 * @param cachePath 软件表情包在软件沙盒的对应目录
 */
sealed class AppBean(
    val packageName: String,
    val providerSecond: String,
    val cachePath: List<CachePath>
) {

    val safPath: String
        get() {
            return "$ANDROID_SAF_PATH$packageName"
        }

    fun getSaveImagePath(secondPath: String): File {
        val file = File(appCtx.externalCacheDir, APP_SHARED_PROVIDER_TOP_PATH)
        val dyFile = File(file, "$providerSecond/$secondPath")
        if (!dyFile.exists()) {
            dyFile.mkdirs()
        }
        return dyFile
    }

    fun isPermissionUri(uri: Uri): Boolean {
        return uri.toString().endsWith(packageName)
    }

}

data class CachePath(
    val path: String,
    val name: String
)