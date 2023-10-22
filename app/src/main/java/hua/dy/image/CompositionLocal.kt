package hua.dy.image

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import hua.dy.image.app.AppBean

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not init")
}

val LocalCurrentAppBean = staticCompositionLocalOf<AppBean> {
    error("App Bean not init")
}