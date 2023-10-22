package hua.dy.image.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.filter
import hua.dy.image.app.AppBean
import hua.dy.image.app.TencentQQBean
import hua.dy.image.bean.ImageBean
import hua.dy.image.db.dyImageDao
import hua.dy.image.utils.scanDyImages
import hua.dy.image.utils.sortValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import splitties.init.appCtx

class DyImageViewModel: ViewModel() {

    val allImages: Flow<PagingData<ImageBean>> =
        Pager(
            config = PagingConfig(
                pageSize = 60,
                enablePlaceholders = true,
                maxSize = 200
            ),
            pagingSourceFactory = ::getImagePagingSource
        ).flow.map {
            it.filter { imageBean ->
                if (_chatImagesStateFlow.value) {
                    imageBean.cachePath == TencentQQBean.cachePath[0]
                } else true
            }
        }.cachedIn(viewModelScope)

    fun refreshDyImages(appBean: AppBean) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                scanDyImages(appBean)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private fun getImagePagingSource(): PagingSource<Int, ImageBean> {
       return when (sortValue) {
            1 -> dyImageDao::getImageListByScanTime
            2 -> dyImageDao::getImageListByFileLength
            else -> dyImageDao::getImageListByFileTime
        }.invoke()
    }

    private val _chatImagesStateFlow = MutableStateFlow(false)
    val chatImageStateFlow = _chatImagesStateFlow.asStateFlow()

    fun changeChatImageState(value: Boolean) {
        _chatImagesStateFlow.value = value
        val text = if (value) {
            "你选择了聊天页面的图片"
        } else {
            "你选择了所有图片"
        }
        Toast.makeText(appCtx, text, Toast.LENGTH_SHORT).show()
    }

}