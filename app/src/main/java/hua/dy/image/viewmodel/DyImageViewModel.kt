package hua.dy.image.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.filter
import hua.dy.image.app.DyAppBean
import hua.dy.image.bean.GIF
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.JPG
import hua.dy.image.bean.Other
import hua.dy.image.bean.PNG
import hua.dy.image.db.dyImageDao
import hua.dy.image.service.FileExplorerService
import hua.dy.image.utils.APP_SHARED_PROVIDER_TOP_PATH
import hua.dy.image.utils.FileExplorerServiceManager
import hua.dy.image.utils.ShizukuUtils
import hua.dy.image.utils.hasDyPermission
import hua.dy.image.utils.scanDyImages
import hua.dy.image.utils.scanDyImagesWithShizuku
import hua.dy.image.utils.sortValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import splitties.init.appCtx
import java.io.File

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
                    imageBean.cachePath == DyAppBean.cachePath[0]
                } else true
            }
        }.cachedIn(viewModelScope)

    fun refreshDyImages() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                startScan()
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private var type = -1

    fun changeType() {
        type = when (type) {
            -1 -> PNG
            PNG, JPG -> type + 1
            GIF -> Other
            else -> -1
        }
        viewModelScope.launch {
            _typeState.emit(getTypeString())
        }
    }

    private fun getTypeString(): String {
        return when (type) {
            PNG -> "PNG"
            JPG -> "JPG"
            GIF -> "GIF"
            Other -> "Other"
            else -> "All"
        }
    }

    private val _typeState = MutableStateFlow(getTypeString())
    val typeState = _typeState.asStateFlow()

    private fun getImagePagingSource(): PagingSource<Int, ImageBean> {
        return if (type == -1) {
            when (sortValue) {
                1 -> dyImageDao.getImageListByScanTime()
                2 -> dyImageDao.getImageListByFileLength()
                else -> dyImageDao.getImageListByFileTime()
            }
        } else {
            when (sortValue) {
                1 -> dyImageDao.getImageListByScanTime(type)
                2 -> dyImageDao.getImageListByFileLength(type)
                else -> dyImageDao.getImageListByFileTime(type)
            }
        }

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


    init {
        viewModelScope.launch {
            val parentDirectory = File(appCtx.externalCacheDir, APP_SHARED_PROVIDER_TOP_PATH)
            val directory = File(parentDirectory, DyAppBean.providerSecond)
            val roomSize = dyImageDao.getImageCount()
            val fileSize = runCatching { directory.listFiles()?.size ?: 0 }.getOrNull() ?: 0
            if (roomSize == 0) return@launch
            if (!directory.exists() || fileSize == 0) {
                Toast.makeText(appCtx, "图片文件被其他文件清理软件清楚, 正在清楚数据库", Toast.LENGTH_SHORT).show()
                dyImageDao.deleteAll()
            }
        }
    }

    private fun startScan() {
        if (ShizukuUtils.isShizukuAvailable) {
            Log.e("TAG", "shizuku")
                Log.e("TAG", "start Scan ${FileExplorerService.service}")
                scanDyImagesWithShizuku()
        } else {
            Log.e("TAG", "saf")
            scanDyImages()
        }
    }

    fun bindService() {
        FileExplorerServiceManager.bindService()
    }

    val hasPermission: Boolean
        get() {
            return if (ShizukuUtils.isShizukuAvailable) {
                ShizukuUtils.isShizukuPermission
            } else {
                hasDyPermission(DyAppBean.packageName)
            }
        }

    var needShizuku: Boolean = true

}