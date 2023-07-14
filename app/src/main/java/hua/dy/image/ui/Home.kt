package hua.dy.image.ui

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.isGif
import hua.dy.image.utils.DY_PACKAGE_NAME
import hua.dy.image.utils.GetDyPermission
import hua.dy.image.utils.dp2Px
import hua.dy.image.utils.hasDyPermission
import hua.dy.image.viewmodel.DyImageViewModel
import kotlinx.coroutines.launch
import splitties.init.appCtx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {

    val viewModel = viewModel(DyImageViewModel::class.java)

    val imageData = viewModel.allImages.collectAsLazyPagingItems()

    var permissionState by remember {
        mutableStateOf(hasDyPermission(DY_PACKAGE_NAME))
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

    val scope = rememberCoroutineScope()

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
                                val permission = hasDyPermission(DY_PACKAGE_NAME)
                                permissionState = permission
                                if (permission) {
                                    viewModel.refreshDyImages()
                                }
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
                // 不加这个重建页面后，这个Column不显示了，暂时不知道为啥
                Text(text = "    ")
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
                            .clickable {
                                scope.launch {
                                    lazyScrollState.scrollToItem(0)
                                }
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
                            .clickable {
                                scope.launch {
                                    lazyScrollState.scrollToItem(imageData.itemCount - 1)
                                }
                            },
                        tint = MaterialTheme.colorScheme.surfaceTint
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
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
                        .transformations(RoundedCornersTransformation(radius = 16.dp.value.dp2Px))
                        .data(item?.imagePath)
                        .build(),
                    imageLoader = item?.imageLoader ?: globalImageLoader,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(8.dp)
                        .pointerInput(index) {
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