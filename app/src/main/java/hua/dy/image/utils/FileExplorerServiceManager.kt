package hua.dy.image.utils


import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import hua.dy.image.BuildConfig
import hua.dy.image.service.FileExplorerService
import hua.dy.image.service.IFileExplorerService
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.UserServiceArgs
import splitties.init.appCtx


object FileExplorerServiceManager {
    private const val TAG = "FileExplorerServiceManager"
    private var isBind = false
    private val USER_SERVICE_ARGS = UserServiceArgs(
        ComponentName(appCtx.packageName, FileExplorerService::class.java.getName())
    ).daemon(false).debuggable(BuildConfig.DEBUG).processNameSuffix("file_explorer_service")
        .version(1)
    private val SERVICE_CONNECTION: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected: ")
            isBind = true
            FileExplorerService.service = IFileExplorerService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected: ")
            isBind = false
            FileExplorerService.service = null
        }
    }

    fun bindService() {
        Log.d(TAG, "bindService: isBind = " + isBind)
        if (!isBind) {
            Log.e("TAG", "startBindService")
            Shizuku.bindUserService(USER_SERVICE_ARGS, SERVICE_CONNECTION)
        }
    }
}