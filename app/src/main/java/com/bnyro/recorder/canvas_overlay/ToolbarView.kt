package com.bnyro.recorder.canvas_overlay

import android.os.Build
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.recorder.R
import com.bnyro.recorder.enums.RecorderState
import com.bnyro.recorder.ui.models.RecorderModel
import com.bnyro.recorder.ui.theme.RecordYouTheme

@Composable
fun ToolbarView(
    modifier: Modifier = Modifier,
    hideCanvas: (Boolean) -> Unit,
    canvasViewModel: CanvasViewModel = viewModel(),
    recorderModel: RecorderModel = viewModel()
) {
    var currentDrawMode by remember { mutableStateOf(DrawMode.Eraser) }
    Card(modifier.height(38.dp).width(175.dp),
//        .blur(radius = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0Xb2708090)
        ),
    ) {
        Row {
            IconButton(
                onClick = {
                    currentDrawMode = if (currentDrawMode == DrawMode.Eraser) {
                        hideCanvas(false)
                        DrawMode.Pen
                    } else {
                        DrawMode.Eraser
                    }
                    canvasViewModel.currentPath.drawMode = currentDrawMode
                }
            ) {
                if (currentDrawMode == DrawMode.Eraser) {
                    Icon(
                        imageVector = Icons.Rounded.Draw,
                        contentDescription = stringResource(R.string.draw_mode)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_eraser_black_24dp),
                        contentDescription = stringResource(R.string.erase_mode)
                    )
                }
            }
            IconButton(onClick = {
                hideCanvas(true)
                canvasViewModel.paths.clear()
            }) {
                Icon(Icons.Rounded.Clear, "Show/Hide Canvas")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                IconButton(onClick = {
                    if (recorderModel.recorderState == RecorderState.PAUSED) {
                        recorderModel.resumeRecording()
                    } else {
                        recorderModel.pauseRecording()
                    }
                }) {
                    if (recorderModel.recorderState == RecorderState.PAUSED) {
                        Icon(
                            Icons.Rounded.PlayArrow,
                            contentDescription = stringResource(id = R.string.resume)
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Pause,
                            contentDescription = stringResource(id = R.string.pause)
                        )
                    }
                }
            }
            IconButton(onClick = {
                recorderModel.stopRecording()
            }) {
                Icon(Icons.Rounded.Stop, stringResource(id = R.string.stop))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToolsScreenPreview() {
    RecordYouTheme {
        Text(text = "哈哈哈哈哈")
        ToolbarView(
            modifier = Modifier,
            hideCanvas = { hide -> false }
        )
    }
}

