package hua.dy.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hua.dy.image.app.TencentQQBean
import hua.dy.image.bean.ImageBean
import hua.dy.image.bean.isGif
import hua.dy.image.ui.Home
import hua.dy.image.ui.imageLoader
import hua.dy.image.ui.theme.DyImageTheme
import hua.dy.image.utils.SHARED_PROVIDER
import hua.dy.image.utils.dp2Px
import hua.dy.image.utils.screenHeight
import hua.dy.image.utils.screenHeightPx
import hua.dy.image.utils.screenWidth
import hua.dy.image.utils.screenWidthPx
import splitties.init.appCtx
import java.io.File
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        setContent {
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !isSystemInDarkTheme()
            )
            DyImageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val appBean = TencentQQBean
                    CompositionLocalProvider(
                        LocalNavController provides navController,
                        LocalCurrentAppBean provides appBean
                    ) {
                        NavHost(navController = navController, startDestination = "home") {
                            route()
                        }
                    }
                }
            }
        }
    }

}

fun NavGraphBuilder.route() {
    composable("home") {
        Home()
    }
}


@Composable
fun SharedDialog(
    dialogState: MutableState<Pair<Boolean, ImageBean?>>
) {
    val context = LocalContext.current
    AlertDialog(
        modifier = Modifier
            .height(screenHeight * 0.4f)
            .width(screenWidth * 0.8f),
        onDismissRequest = {
            dialogState.value = Pair(false, null)
            Toast.makeText(appCtx, "取消分享", Toast.LENGTH_SHORT).show()
        },
        confirmButton = {
            Text(
                text = "分享到其他app",
                modifier = Modifier
                    .clickable {
                        context.shareOtherApp(dialogState.value.second)
                    }
            )
        },
        title = {
            Text(text = "分享表情包")
        },
//        dismissButton = {
//            Text(
//                text = "取消",
//                modifier = Modifier.clickable {
//                    dialogState.value = Pair(false, null)
//                }
//            )
//        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(appCtx)
                        .data(dialogState.value.second?.imagePath)
                        .size(
                            (screenWidthPx * 0.8f).roundToInt(),
                            (screenHeightPx * 0.4f).roundToInt()
                        )
                        .apply {
                            if (dialogState.value.second?.isGif == false)
                                transformations(RoundedCornersTransformation(16.dp.value.dp2Px))
                        }
                        .build(),
                    modifier = Modifier
                        .align(Alignment.Center),
                    imageLoader = dialogState.value.second.imageLoader,
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
            }
        }
    )
}

fun Context.shareOtherApp(
    imageBean: ImageBean?
) {
    if (imageBean == null) {
        return
    }
    val file = File(
        appCtx.externalCacheDir,
        "image_share"
    )
    val uri = FileProvider.getUriForFile(
        appCtx, SHARED_PROVIDER, File(
            file,
            "${imageBean.secondMenu}/${imageBean.fileName}"
        )
    )
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "image/*"
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra(Intent.EXTRA_STREAM, uri)
    startActivity(Intent.createChooser(intent, "分享表情"))
}