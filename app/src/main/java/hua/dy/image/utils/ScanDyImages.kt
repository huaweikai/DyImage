package hua.dy.image.utils

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import hua.dy.image.app.AppBean
import hua.dy.image.app.DyAppBean
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.type
import hua.dy.image.db.dyImageDao
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import splitties.init.appCtx
import java.io.BufferedInputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

private val handlerException = CoroutineExceptionHandler { _, throwable ->
    Log.e("TAG", "异常 $throwable")
}

private val job = SupervisorJob()

private val scope = CoroutineScope(job + handlerException)

const val scopeCount = 4

@Volatile
private var scopeRunningCount = 0

fun scanDyImages(
    appBean: AppBean = DyAppBean
) {
    if (scopeRunningCount > 0) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(appCtx, "正在刷新", Toast.LENGTH_SHORT).show()
        }
    }
    val shared by SharedPreferenceEntrust(appBean.packageName, "")
    val documentDir = DocumentFile.fromTreeUri(appCtx, Uri.parse(shared)) ?: return
    if (!documentDir.exists() || !documentDir.isDirectory) return
    val targetFile = documentDir.findDocument(appBean) ?: return
    if (targetFile.isDirectory && targetFile.listFiles().isEmpty()) return
    targetFile.saveFile(appBean)
}

private fun DocumentFile.getRealScopeCount(): Pair<Int, Int> {
        val fileSum = listFiles().size
        val interval = fileSum.toFloat() / scopeCount
        val scopeCount =  if (fileSize.toFloat() % scopeCount == 0f) {
            scopeCount
        } else {
            if (interval < 1 && interval > 0) 1 else if (interval <= 0) 0 else scopeCount + 1
        }
        return Pair(scopeCount, if (interval < 1) fileSum else interval.toInt())
    }

private fun DocumentFile.saveFile(
    appBean: AppBean
) {
    val (realScopeCount, interval) = getRealScopeCount()
    repeat(realScopeCount) { index ->
        scope.launch(Dispatchers.IO) {
            scopeRunningCount++
            for (i in (index * interval) until ((index + 1) * interval)) {
                this@saveFile.listFiles()[i].saveImage(appBean)
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

private suspend fun DocumentFile.saveImage(
    appBean: AppBean = DyAppBean
) {
    when {
        isDirectory -> {
            listFiles().forEach { document ->
                document.saveImage()
            }
        }

        isFile -> {
            if (length() < fileSize) return
            val md5 = this.md5
            val endType = imageType
            val fileNameWithType = "${this.generalFileName()}.${endType ?: "png"}"
            val newFile = FileProvider.getUriForFile(
                appCtx,
                SHARED_PROVIDER,
                File(appBean.saveImagePath, fileNameWithType)
            )
            appCtx.contentResolver.openOutputStream(newFile)?.use { fos ->
                appCtx.contentResolver.openInputStream(uri)?.use { ins ->
                    ins.copyTo(fos)
                }
            }
            val imageBean = ImageBean(
                md5 = md5,
                imagePath = newFile.toString(),
                fileLength = this.length(),
                fileTime = this.lastModified(),
                fileType = endType.type,
                fileName = fileNameWithType,
                secondMenu = appBean.providerSecond,
                scanTime = System.currentTimeMillis()
            )
            dyImageDao.insert(imageBean)
        }
    }
}

private val DocumentFile.md5: String
    get() {
        val ins = appCtx.contentResolver.openInputStream(uri)
        val md5 = MessageDigest.getInstance("MD5")
        BufferedInputStream(ins, 1024).use {
            md5.update(it.readBytes())
        }
        return BigInteger(1, md5.digest()).toString(16).padStart(32, '0')
    }

val DocumentFile.imageType: String?
    get() {
        val ins = appCtx.contentResolver.openInputStream(uri) ?: return null
        val byteArray = ByteArray(10)
        ins.read(byteArray)
        if (byteArray[0] == 'G'.code.toByte() && byteArray[1] == 'I'.code.toByte() && byteArray[2] == 'F'.code.toByte()) {
            return "gif"
        }
        if (byteArray[1] == 'P'.code.toByte() && byteArray[2] == 'N'.code.toByte() && byteArray[3] == 'G'.code.toByte()) {
            return "png"
        }
        if (byteArray[6] == 'J'.code.toByte() && byteArray[7] == 'F'.code.toByte() && byteArray[8] == 'I'.code.toByte() && byteArray[9] == 'F'.code.toByte()) {
            return "jpg"
        }
        Log.e("FileType", byteArray.map { it.toInt().toChar() }.joinToString("."))
        ins.close()
        return null
    }

/**
 * 以byte为单位
 */
val fileSize by SharedPreferenceEntrust("fileSize", 1024)


fun DocumentFile.generalFileName(): String {
//    val subNameResult = runCatching {
//        this.name?.substring(0,5)
//    }
//    val subName = if (subNameResult.isFailure) {
//        this.name
//    } else subNameResult.getOrThrow()
//    return "${subName}_${simpleDateFormat.format(lastModified())}"
    return "${name?.replace(".cnt", "")}"
}

//private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CHINA)
