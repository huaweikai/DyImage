package hua.dy.image.utils

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import splitties.init.appCtx

object ShizukuUtils {
    private const val SHIZUKU_PACKAGE_NAME = "moe.shizuku.privileged.api"

     private val isShizukuInstalled: Boolean
        get() {
            runCatching {
                appCtx.packageManager.getPackageInfo(SHIZUKU_PACKAGE_NAME, 0)
            }.onFailure {
                return false
            }
            return true
        }

    val isShizukuAvailable: Boolean
        get() = isShizukuInstalled && Shizuku.pingBinder()


    val isShizukuPermission: Boolean
        get() = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED



}