package com.bnyro.recorder.ui.components

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bnyro.recorder.R
import com.bnyro.recorder.ui.common.BlobIconBox
import com.bnyro.recorder.ui.models.RecorderModel

@SuppressLint("ContextCastToActivity")
@Composable
fun RecorderPreview(recordScreenMode: Boolean) {
    val recorderModel: RecorderModel = viewModel(LocalContext.current as ComponentActivity)
    Crossfade(
        modifier = Modifier.fillMaxSize(), targetState = recorderModel.recordedAmplitudes
    ) {
        when (it.isEmpty()) {
            true -> BlobIconBox(
                icon = if (recordScreenMode) R.drawable.ic_screen_record else R.drawable.ic_mic
            )

            false -> AudioVisualizer(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
