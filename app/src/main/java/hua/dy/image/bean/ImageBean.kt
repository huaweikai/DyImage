package hua.dy.image.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val PNG = 0

const val JPG = 1

const val GIF = 2

const val Other = Int.MAX_VALUE

@Entity("dy_image")
data class ImageBean(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val md5: String = "-1",
    @ColumnInfo(name = "image_path")
    val imagePath: String = "",
    @ColumnInfo(name = "file_time")
    val fileTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "file_type")
    val fileType: Int = PNG
)