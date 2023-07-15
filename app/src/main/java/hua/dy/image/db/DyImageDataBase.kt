package hua.dy.image.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hua.dy.image.bean.ImageBean
import splitties.init.appCtx


@Database(
    entities = [ImageBean::class],
    version = 1,
)
abstract class DyImageDataBase: RoomDatabase() {

    abstract val dyImageDao: DyImageDao

}

val dyImageDb: DyImageDataBase by lazy {
    Room.databaseBuilder(appCtx, DyImageDataBase::class.java, "dy_image.db")
        .fallbackToDestructiveMigration()
        .build()
}



val dyImageDao: DyImageDao by lazy {
    dyImageDb.dyImageDao
}