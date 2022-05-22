/*
 * Copyright 2021 The Android Open Source Project
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

package androidx.compose.foundation.text

import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import org.jetbrains.skiko.SkikoKey

internal actual val platformDefaultKeyMapping: KeyMapping = object :KeyMapping {
    override fun map(event: KeyEvent): KeyCommand? {
        if (event.nativeKeyEvent.key == SkikoKey.KEY_BACKSPACE) {
            return KeyCommand.DELETE_PREV_CHAR
        } else {
            return null
        }
//        return KeyCommand.LEFT_WORD
    }
}

internal actual object MappedKeys {
    actual val A: Key = TODO("Implement native  MappedKeys")
    actual val C: Key = TODO("Implement native  MappedKeys")
    actual val H: Key = TODO("Implement native  MappedKeys")
    actual val V: Key = TODO("Implement native  MappedKeys")
    actual val X: Key = TODO("Implement native  MappedKeys")
    actual val Z: Key = TODO("Implement native  MappedKeys")
    actual val Backslash: Key = TODO("Implement native  MappedKeys")
    actual val DirectionLeft: Key = TODO("Implement native  MappedKeys")
    actual val DirectionRight: Key = TODO("Implement native  MappedKeys")
    actual val DirectionUp: Key = TODO("Implement native  MappedKeys")
    actual val DirectionDown: Key = TODO("Implement native  MappedKeys")
    actual val PageUp: Key = TODO("Implement native  MappedKeys")
    actual val PageDown: Key = TODO("Implement native  MappedKeys")
    actual val MoveHome: Key = TODO("Implement native  MappedKeys")
    actual val MoveEnd: Key = TODO("Implement native  MappedKeys")
    actual val Insert: Key = TODO("Implement native  MappedKeys")
    actual val Enter: Key = TODO("Implement native  MappedKeys")
    actual val Backspace: Key = TODO("Implement native  MappedKeys")
    actual val Delete: Key = TODO("Implement native  MappedKeys")
    actual val Paste: Key = TODO("Implement native  MappedKeys")
    actual val Cut: Key = TODO("Implement native  MappedKeys")
    val Copy: Key = TODO("Implement native  MappedKeys")
    actual val Tab: Key = TODO("Implement native  MappedKeys")
}
