package hua.dy.image

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import hua.dy.image.bean.ImageBean
import hua.dy.image.ui.Home
import hua.dy.image.ui.imageLoader
import hua.dy.image.ui.theme.DyImageTheme
import splitties.init.appCtx
import java.io.File

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
                    CompositionLocalProvider(
                        LocalNavController provides navController
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
        dismissButton = {
            Text(
                text = "取消",
                modifier = Modifier.clickable {
                    dialogState.value = Pair(false, null)
                }
            )
        },
        text = {
            Log.e("TAg", "item ${dialogState.value.second}")
            AsyncImage(
                model = dialogState.value.second?.imagePath,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                imageLoader = dialogState.value.second.imageLoader,
                contentDescription = null
            )
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
        appCtx, "hua.dy.image.provider", File(
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