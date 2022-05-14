/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.ui.native

import androidx.compose.runtime.Composable
import androidx.compose.ui.ComposeScene
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.KeyEvent as ComposeKeyEvent
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.toCompose
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.platform.PlatformComponent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.currentMillis
import androidx.compose.ui.input.pointer.PointerEventType
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.skia.Canvas
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkikoView
import org.jetbrains.skiko.SkikoInputEvent
import org.jetbrains.skiko.SkikoKeyboardEvent
import org.jetbrains.skiko.SkikoPointerEvent
import org.jetbrains.skiko.SkikoPointerEventKind
import org.jetbrains.skiko.SkikoTouchEvent
import org.jetbrains.skiko.SkikoTouchEventKind
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.WindowInfoImpl
import androidx.compose.ui.createSkiaLayer
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.window.WindowExceptionHandler

private const val USE_KEYBOARD_EVENT = false

internal class ComposeLayer {
    private var isDisposed = false

    internal val layer = createSkiaLayer()

    @OptIn(ExperimentalComposeUiApi::class)
    var exceptionHandler: WindowExceptionHandler? = null

    @OptIn(ExperimentalComposeUiApi::class)
    private fun catchExceptions(body: () -> Unit) {
        try {
            body()
        } catch (e: Throwable) {
            exceptionHandler?.onException(e) ?: throw e
        }
    }

    inner class ComponentImpl : SkikoView, PlatformComponent {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            val contentScale = layer.contentScale
            canvas.scale(contentScale, contentScale)
            scene.render(canvas/*, (width / contentScale).toInt(), (height / contentScale).toInt()*/, nanoTime)
        }

        override fun onInputEvent(event: SkikoInputEvent) {
            scene.sendInputEvent(event)
        }

        override fun onKeyboardEvent(event: SkikoKeyboardEvent) = catchExceptions {
            if (USE_KEYBOARD_EVENT) {
                println("DIMA onKeyboardEvent event: $event")
                if (isDisposed) return@catchExceptions
                if (scene.sendKeyEvent(ComposeKeyEvent(event))) {
//                    event.consume()
                }
            }
        }

        override fun onTouchEvent(events: Array<SkikoTouchEvent>) {
            val event = events.first()
            when (event.kind) {
                SkikoTouchEventKind.STARTED,
                SkikoTouchEventKind.MOVED,
                SkikoTouchEventKind.ENDED -> {
                    scene.sendPointerEvent(
                        eventType = event.kind.toCompose(),
                        // TODO: account for the proper density.
                        position = Offset(event.x.toFloat(), event.y.toFloat()), // * density,
                        timeMillis = currentMillis(),
                        type = PointerType.Touch,
                        nativeEvent = event
                    )
                }
            }
        }

        override fun onPointerEvent(event: SkikoPointerEvent) {
            scene.sendPointerEvent(
                eventType = event.kind.toCompose(),
                // TODO: account for the proper density.
                position = Offset(event.x.toFloat(), event.y.toFloat()), // * density,
                timeMillis = currentMillis(),
                type = PointerType.Mouse,
                nativeEvent = event
            )
        }

        override val windowInfo = WindowInfoImpl()
    }

    val view = ComponentImpl()

    init {
        layer.skikoView = view
    }

    private val scene = ComposeScene(
        getMainDispatcher(),
        view,
        Density(1f),
        layer::needRedraw
    )

    fun dispose() {
        check(!isDisposed)
        this.layer.detach()
        scene.close()
        _initContent = null
        isDisposed = true
    }

    internal fun setSize(width: Int, height: Int) {
        scene.constraints = Constraints(maxWidth = width, maxHeight = height)
    }

    fun setContent(
        onPreviewKeyEvent: (ComposeKeyEvent) -> Boolean = { false },
        onKeyEvent: (ComposeKeyEvent) -> Boolean = { false },
        content: @Composable () -> Unit
    ) {
        // If we call it before attaching, everything probably will be fine,
        // but the first composition will be useless, as we set density=1
        // (we don't know the real density if we have unattached component)
        _initContent = {
            scene.setContent(
                onPreviewKeyEvent = onPreviewKeyEvent,
                onKeyEvent = onKeyEvent,
                content = content
            )
        }
        initContent()
    }

    private var _initContent: (() -> Unit)? = null

    private fun initContent() {
        // TODO: do we need isDisplayable on SkiaLyer?
        // if (layer.isDisplayable) {
            _initContent?.invoke()
            _initContent = null
        // }
    }
}

