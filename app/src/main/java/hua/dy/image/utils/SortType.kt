package hua.dy.image.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

var sortValue by SharedPreferenceEntrust("sort_type", 0)

val sortList = buildList {
    add("按文件时间排序")
    add("按扫描时间排序")
    add("按文件大小排序")
}

@Composable
fun SortBottomDialog(
    sortState: MutableState<Pair<Boolean, Int>>,
    onclick: (index: Int) -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable {
                    sortState.value = Pair(false, -1)
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(
                            topEnd = 32.dp,
                            topStart = 32.dp
                        )
                    )
                    .padding(
                        vertical = 16.dp,
                        horizontal = 16.dp
                    )
                    .height(IntrinsicSize.Min)
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = "排序", modifier = Modifier.align(Alignment.CenterHorizontally))
                sortList.forEachIndexed { index, s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onclick.invoke(index)
                                sortState.value = Pair(false, index)
                            }
                            .height(50.dp)
                    ) {
                        Text(
                            text = s,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(8f)
                        )
                        if (sortState.value.second == index) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(2f),
                                contentDescription = "Check",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}