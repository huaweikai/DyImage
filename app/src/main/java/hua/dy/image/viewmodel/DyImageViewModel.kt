package hua.dy.image.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.filter
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.isGif
import hua.dy.image.bean.isJpg
import hua.dy.image.bean.isPng
import hua.dy.image.db.dyImageDao
import hua.dy.image.utils.scanDyImages
import hua.dy.image.utils.sortValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DyImageViewModel: ViewModel() {

    private var _allImages: Flow<PagingData<ImageBean>> =
        Pager(
            config = PagingConfig(
                pageSize = 60,
                enablePlaceholders = true,
                maxSize = 200
            ),
            pagingSourceFactory = getImagePagingSource()
        ).flow.cachedIn(viewModelScope)

    val allImages get() = _allImages


    val gifImage = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        ),
        pagingSourceFactory = dyImageDao::getImageListByFileTime
    ).flow.map { pageData ->
        pageData.filter {
            it.isGif
        }
    }.cachedIn(viewModelScope)

    val pngImage = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        ),
        pagingSourceFactory = dyImageDao::getImageListByFileTime
    ).flow.map { pageData ->
        pageData.filter {
            it.isPng
        }
    }.cachedIn(viewModelScope)

    val jpgImage = Pager(
        config = PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        ),
        pagingSourceFactory = dyImageDao::getImageListByFileTime
    ).flow.map { pageData ->
        pageData.filter {
            it.isJpg
        }
    }.cachedIn(viewModelScope)

    fun refreshDyImages() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                scanDyImages()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun refreshImage() {
        viewModelScope.launch {
            _allImages = Pager(
                config = PagingConfig(
                    pageSize = 60,
                    enablePlaceholders = true,
                    maxSize = 200
                ),
                pagingSourceFactory = getImagePagingSource()
            ).flow.cachedIn(viewModelScope)
        }
    }

    private fun getImagePagingSource(): () -> PagingSource<Int, ImageBean> {
       return when (sortValue) {
            1 -> dyImageDao::getImageListByScanTime
            2 -> dyImageDao::getImageListByFileLength
            else -> dyImageDao::getImageListByFileTime
        }
    }

}