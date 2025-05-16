package com.bnyro.recorder.ui.screens

import android.annotation.SuppressLint
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.recorder.R
import com.bnyro.recorder.enums.RecorderState
import com.bnyro.recorder.enums.RecorderType
import com.bnyro.recorder.ui.Destination
import com.bnyro.recorder.ui.common.ClickableIcon
import com.bnyro.recorder.ui.models.RecorderModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    initialRecorder: RecorderType,
    onNavigate: (Destination) -> Unit,
    @SuppressLint("ContextCastToActivity") recorderModel: RecorderModel = viewModel(LocalContext.current as ComponentActivity)
) {
    val pagerState =
        rememberPagerState(initialPage = if (initialRecorder == RecorderType.VIDEO) 1 else 0) { 2 }
    val scope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = { Text(stringResource(R.string.app_name)) }, actions = {
            ClickableIcon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings)
            ) {
                onNavigate(Destination.Settings)
            }
            ClickableIcon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = stringResource(R.string.recordings)
            ) {
                onNavigate(Destination.RecordingPlayer)
            }

            IconButton(
                onClick = {
                    recorderModel.toDesk(view)
                }
            ) {
                Icon(Icons.Default.Window, "debug")
            }
        })
    }, bottomBar = {
        Column {
            AnimatedVisibility(recorderModel.recorderState == RecorderState.IDLE) {
                NavigationBar {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = stringResource(
                                    id = R.string.record_sound
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.record_sound)) },
                        selected = (pagerState.currentPage == 0),
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            scope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = stringResource(
                                    id = R.string.record_screen
                                )
                            )
                        },
                        label = { Text(stringResource(R.string.record_screen)) },
                        selected = (pagerState.currentPage == 1),
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                }
            }
        }
    }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { index ->
                RecorderView(recordScreenMode = (index == 1))
            }
        }
    }
}
