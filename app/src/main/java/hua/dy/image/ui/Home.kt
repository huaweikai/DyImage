package hua.dy.image.ui

import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import hua.dy.image.SharedDialog
import hua.dy.image.app.DyAppBean
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.isGif
import hua.dy.image.utils.GetDyPermission
import hua.dy.image.utils.SortBottomDialog
import hua.dy.image.utils.dp2Px
import hua.dy.image.utils.hasDyPermission
import hua.dy.image.utils.screenHeightPx
import hua.dy.image.utils.sortValue
import hua.dy.image.viewmodel.DyImageViewModel
import kotlinx.coroutines.launch
import splitties.init.appCtx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {

    val sortImageState = remember {
        mutableStateOf(Pair(false, -1))
    }

    val context = LocalContext.current

    val viewModel = viewModel(DyImageViewModel::class.java)

    val imageData = viewModel.allImages.collectAsLazyPagingItems()

    var permissionState by remember {
        mutableStateOf(hasDyPermission(DyAppBean.packageName))
    }

    if (!permissionState) {
        GetDyPermission()
    }

    val dialogState = remember {
        mutableStateOf(Pair<Boolean, ImageBean?>(false, null))
    }

    var imageNumber by remember {
        mutableIntStateOf(0)
    }

    val animationNumber by animateIntAsState(
        targetValue = imageNumber,
        label = ""
    )

    LaunchedEffect(key1 = imageData.itemCount) {
        imageNumber = imageData.itemCount
    }

    val lazyScrollState = rememberLazyGridState()

    val typeStringState = viewModel.typeState.collectAsState()

    LaunchedEffect(key1 = typeStringState.value) {
        Toast.makeText(context, "现在检索的是 ${typeStringState.value} 类型", Toast.LENGTH_SHORT)
            .show()
    }

    val scope = rememberCoroutineScope()

    val chatMainState = viewModel.chatImageStateFlow.collectAsState()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "抖音表情包 总数: $animationNumber")
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "刷新",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                val permission = hasDyPermission(DyAppBean.packageName)
                                permissionState = permission
                                if (permission) {
                                    viewModel.refreshDyImages()
                                }
                            }
                    )
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "排序",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                sortImageState.value = Pair(true, sortValue)
                            }
                    )
                }
            )
        },
        floatingActionButton = {
            Column(
                modifier = Modifier
                    .padding(bottom = 16.dp)
            ) {
                TextButton(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(16.dp)
                        ),
                    onClick = {
                        viewModel.changeType()
                        imageData.refresh()
                    }
                ) {
                    Text(text = typeStringState.value, color = MaterialTheme.colorScheme.surfaceTint)
                }
                AnimatedVisibility(visible = lazyScrollState.canScrollBackward) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowUp,
                        contentDescription = "Up",
                        modifier = Modifier
                            .padding(8.dp)
                            .width(48.dp)
                            .height(48.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        scope.launch {
                                            lazyScrollState.scrollToItem(0)
                                        }
                                    },
                                    onTap = {
                                        scope.launch {
                                            lazyScrollState.animateScrollBy(
                                                -screenHeightPx * 0.8f
                                            )
                                        }
                                    }
                                )
                            },
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
                AnimatedVisibility(visible = lazyScrollState.canScrollForward) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Down",
                        modifier = Modifier
                            .padding(8.dp)
                            .width(48.dp)
                            .height(48.dp)
                            .background(
                                MaterialTheme.colorScheme.background,
                                shape = CircleShape
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        scope.launch {
                                            lazyScrollState.scrollToItem(imageData.itemCount - 1)
                                        }
                                    },
                                    onTap = {
                                        scope.launch {
                                            lazyScrollState.animateScrollBy(
                                                screenHeightPx * 0.8f
                                            )
                                        }
                                    }
                                )
                            },
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
                IconToggleButton(
                    checked = chatMainState.value,
                    onCheckedChange = {
                        viewModel.changeChatImageState(it)
                        imageData.refresh()
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(48.dp)
                        .height(48.dp)
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        ),
                ) {
                    Icon(
                        imageVector = if (chatMainState.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(paddingValues),
            contentPadding = PaddingValues(
                bottom = 100.dp
            ),
            state = lazyScrollState
        ) {
            items(imageData.itemCount) { index ->
                val item = imageData[index]
                AsyncImage(
                    model = ImageRequest.Builder(appCtx)
                        .data(item?.imagePath)
                        .apply {
                            if (item?.isGif == false)
                                transformations(RoundedCornersTransformation(16.dp.value.dp2Px))
                        }
                        .build(),
                    imageLoader = item?.imageLoader ?: globalImageLoader,
                    modifier = Modifier
                        .padding(8.dp)
                        .aspectRatio(1f)
                        .pointerInput(index, item?.md5) {
                            detectTapGestures(
                                onLongPress = {
                                    dialogState.value = Pair(true, item)
                                }
                            )
                        },
                    contentScale = ContentScale.Crop,
                    contentDescription = null
                )
            }
        }
        if (dialogState.value.first && dialogState.value.second != null) {
            SharedDialog(dialogState = dialogState)
        }

    }

    AnimatedVisibility(visible = sortImageState.value.first) {
        SortBottomDialog(
            sortImageState,
            onclick = {
                sortValue = it
                imageData.refresh()
            }
        )
    }

}


val ImageBean?.imageLoader: ImageLoader
    get() {
        return if (this?.isGif == true) gifImageLoader else globalImageLoader
    }

val globalImageLoader = ImageLoader.Builder(appCtx)
    .build()

val gifImageLoader = ImageLoader.Builder(appCtx)
    .components {
        if (SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
    }
    .build()