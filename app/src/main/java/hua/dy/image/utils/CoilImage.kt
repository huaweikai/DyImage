package hua.dy.image.utils

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

private const val TAG = "Coil"

@Composable
fun CoilImage(
    url: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = DefaultAlpha,
    shape: Shape? = null,
    colorFilter: ColorFilter? = null,
    builder: ImageRequest.Builder.() -> Unit = {},
    onSuccess: (() -> Unit)? = null,
    onError: @Composable () -> Unit = {
    },
    onLoading: @Composable () -> Unit = {
        CircularProgressIndicator()
    }
) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(url)
            .apply(builder)
            .build()
    )
    Box(
        modifier = modifier then if (shape != null) Modifier.clip(shape) else Modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter, contentDescription = contentDescription,
            modifier = Modifier.matchParentSize(),
            alignment = alignment,
            contentScale = contentScale,
            alpha = alpha, colorFilter = colorFilter
        )
        Crossfade(targetState = painter.state, label = "") { state ->
            when (state) {
                is AsyncImagePainter.State.Empty -> {
                    Log.e(TAG, "CoilImage: Empty", )

                }

                is AsyncImagePainter.State.Loading -> {
                    Log.e(TAG, "CoilImage: Loading", )
                    onLoading()
                }

                is AsyncImagePainter.State.Success -> {
                    Log.e(TAG, "CoilImage: Success", )
                    onSuccess?.invoke()
                }

                is AsyncImagePainter.State.Error -> {
                    Log.e(TAG, "CoilImage: Error ${state.result.throwable}", )
                    onError()
                }
            }
        }
    }
}