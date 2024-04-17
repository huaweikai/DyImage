package hua.dy.image.service

import android.os.Process
import android.os.RemoteException
import android.util.Log
import hua.dy.image.bean.FileBean
import hua.dy.image.bean.ImageBean
import java.io.File

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

    private fun File.toFileBean(): FileBean {
        return FileBean(
            name = name,
            path = path,
            length = length(),
            lastModified = lastModified(),
            isDirectory = isDirectory
        )
    }


    companion object {
        private const val TAG = "FileExplorerService"

        var service: IFileExplorerService? = null
    }
}