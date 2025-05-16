package com.bnyro.recorder.ui.components

import android.view.SoundEffectConstants
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.bnyro.recorder.ui.models.PlayerModel

@Composable
fun MiniPlayer(inputFile: DocumentFile, playerModel: PlayerModel = viewModel()) {
    val view = LocalView.current
    DisposableEffect(inputFile) {
        with(playerModel.player) {
            val mediaItem = MediaItem.Builder().setUri(inputFile.uri).build()
            setMediaItem(mediaItem)
            playWhenReady = true
            prepare()
            onDispose {
                stop()
            }
        }
    }
    ElevatedCard(
        modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 15.dp, horizontal = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val fileName = inputFile.name.orEmpty()
                Text(
                    modifier = Modifier.weight(1f),
                    text = fileName.substringBeforeLast(".").takeIf {
                        it.isNotBlank()
                    } ?: fileName
                )
                with(playerModel.player) {

                    var playState by remember { mutableStateOf(false) }

                    DisposableEffect(key1 = this) {
                        val listener = object : Player.Listener {
                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                playState = isPlaying
                            }
                        }
                        addListener(listener)
                        onDispose {
                            removeListener(listener)
                        }
                    }
                    FloatingActionButton(
                        onClick = {
                            view.playSoundEffect(SoundEffectConstants.CLICK)
                            playPause()
                        }
                    ) {
                        if (playState) {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = stringResource(id = com.bnyro.recorder.R.string.pause)
                            )
                        } else {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = stringResource(id = com.bnyro.recorder.R.string.play)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            PlayerController(exoPlayer = playerModel.player)
        }
    }
}
