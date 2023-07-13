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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(imageBean: ImageBean)

    @Query("SELECT * FROM dy_image ORDER BY file_time COLLATE NOCASE ASC")
    fun getImageList(): PagingSource<Int, ImageBean>

    @Query("DELETE FROM dy_image where id = :id")
    suspend fun deleteImage(id: Int): Int

    @Delete
    suspend fun deleteImage(imageBean: ImageBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cheeses: List<ImageBean>)

}