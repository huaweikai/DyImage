package hua.dy.image.service

import android.os.RemoteException
import android.util.Log
import hua.dy.image.bean.FileBean
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.type
import java.io.BufferedInputStream
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

class FileExplorerService : IFileExplorerService.Stub() {
    @Throws(RemoteException::class)
    override fun listFiles(path: String?): List<FileBean> {
        if (path == null) return emptyList()
        val file = File(path)
        if (file.isFile) return emptyList()
        return file.listFiles()?.map { it.toFileBean() } ?: emptyList()
    }

    override fun getFileBean(path: String?): FileBean {
        if (path == null) throw NullPointerException("Path is Empty")
        return File(path).toFileBean()
    }

    override fun copyToMyFile(
        bean: FileBean?,
        fileSize: Long,
        cacheIndex: Int,
        providerSecond: String?,
        saveImagePath: String?,
        cachePath: List<String>?
    ): ImageBean {
        if (bean == null) throw NullPointerException("Bean is Empty")
        if ((bean.length ?: 0L) < fileSize) throw Exception("File is so big")
        val md5 = bean.md5
        val endType = bean.imageType
        val fileNameWithType = "${bean.generalFileName()}.${endType ?: "png"}"
        val generalFilePath = File(saveImagePath, fileNameWithType)
        generalFilePath.outputStream().use { fos ->
            File(bean.path!!).inputStream().use { ins ->
                ins.copyTo(fos)
            }
        }
        return ImageBean(
            md5 = md5,
            imagePath = generalFilePath.toString(),
            fileLength = bean.length ?: 0L,
            fileTime = bean.lastModified ?: 0L,
            fileType = endType.type,
            fileName = fileNameWithType,
            secondMenu = providerSecond ?: "",
            scanTime = System.currentTimeMillis(),
            cachePath = cachePath?.getOrNull(cacheIndex) ?: cachePath?.first() ?: ""
        )
    }

    private fun File.toFileBean(): FileBean {
        return FileBean(
            name = name,
            path = path,
            length = length(),
            lastModified = lastModified(),
            isDirectory = isDirectory
        )
    }

    private val FileBean.md5: String
        get() {
            val md5 = MessageDigest.getInstance("MD5")
            BufferedInputStream(File(path!!).inputStream(), 1024).use {
                md5.update(it.readBytes())
            }
            return BigInteger(1, md5.digest()).toString(16).padStart(32, '0')
        }

    val FileBean.imageType: String?
        get() {
            val ins = File(path!!).inputStream()
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
            Log.e("FileType2", byteArray.map { it.toInt().toChar() }.joinToString("."))
            ins.close()
            return null
        }

    fun FileBean.generalFileName(): String {
//    val subNameResult = runCatching {
//        this.name?.substring(0,5)
//    }
//    val subName = if (subNameResult.isFailure) {
//        this.name
//    } else subNameResult.getOrThrow()
//    return "${subName}_${simpleDateFormat.format(lastModified())}"
        return "${name?.replace(".cnt", "")}"
    }


    companion object {
        private const val TAG = "FileExplorerService"

        var service: IFileExplorerService? = null
    }
}