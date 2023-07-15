package hua.dy.image.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hua.dy.image.bean.ImageBean

@Dao
interface DyImageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(imageBean: ImageBean)

    @Query("SELECT * FROM dy_image ORDER BY file_time COLLATE NOCASE DESC")
    fun getImageListByFileTime(): PagingSource<Int, ImageBean>

    @Query("SELECT * FROM dy_image ORDER BY file_length COLLATE NOCASE DESC")
    fun getImageListByFileLength(): PagingSource<Int, ImageBean>

    @Query("SELECT * FROM dy_image ORDER BY scan_time COLLATE NOCASE DESC")
    fun getImageListByScanTime(): PagingSource<Int, ImageBean>

    @Delete
    suspend fun deleteImage(imageBean: ImageBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cheeses: List<ImageBean>)

    @Query("SELECT count(*) FROM dy_image WHERE md5 = :md5")
    suspend fun selectMd5Exist(md5: String): Int

}