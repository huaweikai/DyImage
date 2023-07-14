// 用Suppress 忽略拼写错误
@file:Suppress("SpellCheckingInspection")
package hua.dy.image.utils

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.documentfile.provider.DocumentFile
import hua.dy.image.SharedPreferenceEntrust
import splitties.init.appCtx
import java.io.File

const val ANDROID_SAF_PATH = "content://com.android.externalstorage.documents/document/primary%3AAndroid%2Fdata%2F"


/**
 * 抖音包名
 */
const val DY_PACKAGE_NAME = "com.ss.android.ugc.aweme"

/**
 * 抖音SAF路径
 */
const val DY_SAF_PATH = "${ANDROID_SAF_PATH}$DY_PACKAGE_NAME"

@Composable
fun GetDyPermission(
    packageName: String = DY_PACKAGE_NAME
) {
    var packShared by SharedPreferenceEntrust(packageName, "")
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) {
            Toast.makeText(appCtx, "Permission denied", Toast.LENGTH_SHORT).show()
            return@rememberLauncherForActivityResult
        }
        val modeFlags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        appCtx.contentResolver.takePersistableUriPermission(uri, modeFlags)
        packShared = uri.toString()
    }
    LaunchedEffect(Unit) {

        if (packShared.isBlank() || !hasDyPermission(packageName)) {
            launcher.launch(Uri.parse(DY_SAF_PATH))
        }
    }
}

fun hasDyPermission(packageName: String): Boolean {
    val permissionUris = appCtx.contentResolver.persistedUriPermissions.map {
        it.uri.toString().split("data%2F", ignoreCase = true).last()
    }
    return permissionUris.indexOf(packageName) != -1
}

val DyImagePath by lazy {
    File(
        appCtx.filesDir,
        "dyImage"
    ).apply {
        if (!exists()) {
            mkdirs()
        }
    }
}


fun DocumentFile.findDocument(
    path: String
): DocumentFile? {
    val pathList = path.split("/").filter { it.isNotBlank() }
    var document = this
    pathList.forEach {
        document.findFile(it)?.let { file ->
            document = file
        } ?: return null
    }
    return document
}