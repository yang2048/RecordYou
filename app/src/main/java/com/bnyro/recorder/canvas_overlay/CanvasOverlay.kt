package com.bnyro.recorder.canvas_overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.bnyro.recorder.ui.theme.RecordYouTheme

@SuppressLint("ClickableViewAccessibility")
class CanvasOverlay(context: Context) {
    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val canvasView = ComposeView(context).apply {
        val activity = context as ComponentActivity
        setViewTreeLifecycleOwner(activity)
        setViewTreeViewModelStoreOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)
        setContent {
            RecordYouTheme() {
                MainCanvas()
            }
        }
    }
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    @OptIn(ExperimentalComposeUiApi::class)
    private val toolbarView = ComposeView(context).apply {
        val activity = context as ComponentActivity
        setViewTreeLifecycleOwner(activity)
        setViewTreeViewModelStoreOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)
        setContent {
            RecordYouTheme {
                ToolbarView(
                    Modifier.motionEventSpy { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                initialX = toolbarViewParams.x
                                initialY = toolbarViewParams.y
                                initialTouchX = event.rawX
                                initialTouchY = event.rawY
                            }

                            MotionEvent.ACTION_MOVE -> {
                                val newX = (
                                    initialX -
                                        (event.rawX - initialTouchX).toInt()
                                    )
                                val newY = (
                                    initialY +
                                        (event.rawY - initialTouchY).toInt()
                                    )
                                toolbarViewParams.x = newX
                                toolbarViewParams.y = newY
                                windowManager.updateViewLayout(this@apply, toolbarViewParams)
                            }
                        }
                    },
                    hideCanvas = { hide ->
                        if (hide) {
                            hideCanvas()
                        } else {
                            showCanvas()
                        }
                    }
                )
            }
        }
    }

    private val toolbarViewParams =
        getWindowLayoutParams(WindowManager.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 0
            y = 0
        }

    fun show() {
        hideCanvas()
        try {
            if (canvasView.windowToken == null && canvasView.parent == null) {
                val params = getWindowLayoutParams(WindowManager.LayoutParams.MATCH_PARENT)
                windowManager.addView(canvasView, params)
            }
            if (toolbarView.windowToken == null && toolbarView.parent == null) {
                toolbarViewParams.format = PixelFormat.RGBA_8888; // 设置view效果为背景透明
//                toolbarViewParams.type = WindowManager.LayoutParams.TYPE_STATUS_BAR;
//                toolbarViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;//后面窗口仍然可以处理点击事件
                toolbarViewParams.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                toolbarViewParams.setTitle("Toolbar");
                windowManager.addView(toolbarView, toolbarViewParams)
            }
        } catch (e: Exception) {
            Log.e("Show Overlay", e.toString())
        }
    }

    private fun getWindowLayoutParams(size: Int): WindowManager.LayoutParams {
        return WindowManager.LayoutParams(
            size,
            size,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
    }

    fun showCanvas() {
        canvasView.isVisible = true
    }

    fun hideCanvas() {
        canvasView.isInvisible = true
    }

    fun remove() {
        try {
            windowManager.removeView(canvasView)
            canvasView.invalidate()
            windowManager.removeView(toolbarView)
            toolbarView.invalidate()
        } catch (e: Exception) {
            Log.e("Remove Overlay", e.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    RecordYouTheme() {
        MainCanvas()
    }
}
