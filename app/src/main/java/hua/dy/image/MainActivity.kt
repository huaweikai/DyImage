package hua.dy.image

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import hua.dy.image.ui.theme.DyImageTheme
import hua.dy.image.utils.GetDyPermission
import hua.dy.image.viewmodel.DyImageViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    
    val viewModel = viewModel(DyImageViewModel::class.java)

    val imageData = viewModel.allImages.collectAsLazyPagingItems()
    GetDyPermission()

    val dialogState = remember {
        mutableStateOf(Pair(false, ""))
    }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "抖音表情包")
                },
                actions = {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "刷新",
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                viewModel.refreshDyImages()
                            }
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(paddingValues)
        ) {
            items(imageData.itemCount) {
                AsyncImage(
                    model = imageData[it]?.imagePath,
                    modifier = Modifier
                        .height(120.dp)
                        .padding(8.dp)
                        .clickable {
                            dialogState.value = Pair(true, imageData[it]?.imagePath ?:"")
                        },
                    contentDescription = null
                )
            }
        }
        if (dialogState.value.first) {
            SharedDialog(dialogState = dialogState)
        }

    }
    
}


@Composable
fun SharedDialog(
    dialogState: MutableState<Pair<Boolean, String>>
) {
    Dialog(
        onDismissRequest =  {}
    ) {
        Text(
            text = dialogState.value.second,
            modifier = Modifier
                .clickable {
                    dialogState.value = Pair(false, "")
                },
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}