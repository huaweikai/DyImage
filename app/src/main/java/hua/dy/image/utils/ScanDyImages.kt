package hua.dy.image.utils

import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import hua.dy.image.SharedPreferenceEntrust
import hua.dy.image.bean.ImageBean
import splitties.init.appCtx
import java.io.BufferedInputStream


suspend fun scanDyImages(
    packageName: String = DY_PACKAGE_NAME
): List<ImageBean> {
    val shared by SharedPreferenceEntrust(packageName, "")
    val dirUri = Uri.parse(shared)
    val documentDir = DocumentFile.fromTreeUri(appCtx, dirUri) ?: return emptyList()
    if (!documentDir.exists() || !documentDir.isDirectory) return emptyList()
    documentDir.listFiles().forEach {
        saveImage(it)
    }
    return emptyList()
}

val array = ByteArray(10)

private fun saveImage(file: DocumentFile): List<ImageBean> {
    when {
        file.isDirectory -> {
            file.listFiles().forEach {
                saveImage(it)
            }
        }
        file.isFile -> {
            appCtx.contentResolver.openInputStream(file.uri).use {
                BufferedInputStream(it, 10).use {
                    it.read(array )
                    getImageType(file.name, array)
                }
            }
        }
    }
    return emptyList()
}

fun getImageType(
    fileName: String?,
    byteArray: ByteArray
) {
    if (byteArray[0] == 'G'.code.toByte() && byteArray[1] == 'I'.code.toByte() && byteArray[2] == 'F'.code.toByte()) {
        Log.e("TAG", "$fileName gif")
    }
    if (byteArray[1] == 'P'.code.toByte() && byteArray[2] == 'N'.code.toByte() && byteArray[3] == 'G'.code.toByte()) {
        Log.e("TAG", "$fileName png")
    }
    if (byteArray[6] == 'J'.code.toByte() && byteArray[7] == 'F'.code.toByte() && byteArray[8] == 'I'.code.toByte() && byteArray[9] == 'F'.code.toByte()) {
        Log.e("TAG", "$fileName jpg")
    }
}