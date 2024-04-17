package hua.dy.image

import android.app.Application
import android.util.Log
import hua.dy.image.utils.FileExplorerServiceManager
import hua.dy.image.utils.ShizukuUtils
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import rikka.sui.Sui

class DyImgApplication: Application() {

    companion object {
        private val isSui = Sui.init(BuildConfig.APPLICATION_ID)
    }

    override fun onCreate() {
        super.onCreate()
        Shizuku.addBinderReceivedListener {
            if (ShizukuUtils.isShizukuAvailable) {
                FileExplorerServiceManager.bindService()
            }
        }
        Shizuku.addBinderDeadListener {
            Log.e("TAG", "binder dead")
        }
    }



}