package hua.dy.image

import android.app.Application
import android.util.Log
import hua.dy.image.utils.FileExplorerServiceManager
import hua.dy.image.utils.ShizukuUtils
import rikka.shizuku.Shizuku

class DyImgApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Shizuku.addBinderReceivedListener {
            if (ShizukuUtils.isShizukuAvailable && ShizukuUtils.isShizukuPermission) {
                FileExplorerServiceManager.bindService()
            }
        }
        Shizuku.addBinderDeadListener {
            Log.e("TAG", "binder dead")
        }
    }



}