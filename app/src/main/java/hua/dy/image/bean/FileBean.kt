package hua.dy.image.bean

import android.os.Parcel
import android.os.Parcelable
import hua.dy.image.service.FileExplorerService
import kotlinx.parcelize.Parcelize
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import java.io.File

data class FileBean(
    val name: String?,
    val path: String?,
    val length: Long?,
    val lastModified: Long?,
    val isDirectory: Boolean?
): Parcelable {

    constructor(parcel: Parcel) : this(
        name = parcel.readString(),
        path = parcel.readString(),
        length = parcel.readLong(),
        lastModified = parcel.readLong(),
        isDirectory = parcel.readByte() == 1.toByte()
    )

    fun listFiles(): List<FileBean> {
        val service = FileExplorerService.service ?: return emptyList()
        return service.listFiles(path)
    }

    fun findFile(path: String?): FileBean? {
        val service = FileExplorerService.service ?: return null
        return service.getFileBean(this.path + "/$path")
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeLong(length ?: 0L)
        dest.writeLong(lastModified ?: 0L)
        dest.writeByte(if (isDirectory == true) 1.toByte() else 0.toByte())
    }

    companion object CREATOR : Parcelable.Creator<FileBean> {
        override fun createFromParcel(parcel: Parcel): FileBean {
            return FileBean(parcel)
        }

        override fun newArray(size: Int): Array<FileBean?> {
            return arrayOfNulls(size)
        }
    }

    val isFile: Boolean get() = isDirectory?.not() ?: true


}