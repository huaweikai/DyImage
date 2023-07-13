package hua.dy.image.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import hua.dy.image.bean.ImageBean
import hua.dy.image.db.dyImageDao
import hua.dy.image.utils.scanDyImages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DyImageViewModel: ViewModel() {

    val allImages = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        )
    ) {
        dyImageDao.getImageList()
    }.flow.cachedIn(viewModelScope)

    fun insertImage(imageBean: ImageBean) {
        viewModelScope.launch {
            dyImageDao.insert(imageBean)
        }
    }

    fun deleteImage(imageBean: ImageBean) {
        viewModelScope.launch {
            dyImageDao.deleteImage(imageBean)
        }
    }

    fun deleteImage(id: Int) {
        viewModelScope.launch {
            dyImageDao.deleteImage(id)
        }
    }

    fun insertImages(images: List<ImageBean>) {
        viewModelScope.launch {
            dyImageDao.insert(images)
        }
    }

    fun refreshDyImages() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                scanDyImages()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

}