package hua.dy.image.utils

import android.util.Log
import android.widget.Toast
import hua.dy.image.app.AppBean
import hua.dy.image.app.DY_FILE_PATH
import hua.dy.image.app.DyAppBean
import hua.dy.image.bean.FileBean
import hua.dy.image.db.dyImageDao
import hua.dy.image.service.FileExplorerService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import splitties.init.appCtx

private val handlerException = CoroutineExceptionHandler { _, throwable ->
    Log.e("TAG", "异常 $throwable")
}

private val job = SupervisorJob()

private val scope = CoroutineScope(job + handlerException)

@Volatile
private var scopeRunningCount = 0

fun scanDyImagesWithShizuku(
    appBean: AppBean = DyAppBean
) {
    if (scopeRunningCount > 0) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(appCtx, "正在刷新", Toast.LENGTH_SHORT).show()
        }
    }
    val service = FileExplorerService.service ?: return
    repeat(appBean.cachePath.size) { index ->
        val path = appBean.cachePath[index]
        val targetFileBean = service.getFileBean(DY_FILE_PATH)
        val targetFile = targetFileBean?.findDocument(path) ?: return@repeat
        if (targetFile.isDirectory == true && targetFile.listFiles().isEmpty()) return@repeat
        targetFile.saveFile(index, appBean)
    }
}

private fun FileBean.getRealScopeCount(): Pair<Int, Int> {
    val fileSum = listFiles().size
    val interval = fileSum.toFloat() / scopeCount
    val scopeCount =  if (fileSize.toFloat() % scopeCount == 0f) {
        scopeCount
    } else {
        if (interval < 1 && interval > 0) 1 else if (interval <= 0) 0 else scopeCount + 1
    }
    return Pair(scopeCount, if (interval < 1) fileSum else interval.toInt())
}

private fun FileBean.saveFile(
    cacheIndex: Int,
    appBean: AppBean
) {
    val (realScopeCount, interval) = getRealScopeCount()
    repeat(realScopeCount) { index ->
        scope.launch(Dispatchers.IO) {
            scopeRunningCount++
            for (i in (index * interval) until ((index + 1) * interval)) {
                this@saveFile.listFiles()[i].saveImage(cacheIndex, appBean)
            }
        }.invokeOnCompletion {
            if (--scopeRunningCount == 0) {
                scope.launch(Dispatchers.Main) {
                    Toast.makeText(appCtx, "刷新完成", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

private suspend fun FileBean.saveImage(
    cacheIndex: Int,
    appBean: AppBean = DyAppBean
) {
    when {
        isDirectory == true -> {
            listFiles().forEach { document ->
                document.saveImage(cacheIndex)
            }
        }

        isFile -> {
            val imageBean = FileExplorerService.service?.copyToMyFile(
                this,
                fileSize.toLong(),
                cacheIndex,
                appBean.providerSecond,
                appBean.saveImagePath.path,
                appBean.cachePath
            ) ?: return
            dyImageDao.insert(imageBean)
        }
    }
}