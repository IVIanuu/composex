/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.compose

import android.view.View
import androidx.compose.remember
import com.ivianuu.compose.internal.checkIsComposing
import com.ivianuu.compose.internal.sourceLocation

@PublishedApi
internal class RemoveCallbackHolder(var removeCallback: (() -> Unit)? = null)

inline fun <T : View> ComponentContext<T>.onBindView(
    noinline callback: (T) -> Unit
) {
    checkIsComposing()
    onBindViewImpl(key = sourceLocation(), inputs = null, callback = callback)
}

inline fun <T : View> ComponentContext<T>.onBindView(
    vararg inputs: Any?,
    noinline callback: (T) -> Unit
) {
    checkIsComposing()
    onBindViewImpl(key = sourceLocation(), inputs = inputs, callback = callback)
}

@PublishedApi
internal fun <T : View> ComponentContext<T>.onBindViewImpl(
    key: Any,
    inputs: Array<out Any?>?,
    callback: (T) -> Unit
) {
    checkIsComposing()
    key(key) {
        val component = currentComponent<T>()
        val removeCallbackHolder = memo { RemoveCallbackHolder(null) }
        if (inputs != null) {
            composer.remember(*inputs) {
                removeCallbackHolder.removeCallback?.invoke()
                removeCallbackHolder.removeCallback = component.onBindView(callback)
            }
        } else {
            composer.changed(callback)
            removeCallbackHolder.removeCallback?.invoke()
            removeCallbackHolder.removeCallback = component.onBindView(callback)
        }
        onDispose { removeCallbackHolder.removeCallback?.invoke() }
    }
}

inline fun <T : View> ComponentContext<T>.onUnbindView(
    noinline callback: (T) -> Unit
) {
    checkIsComposing()
    onUnbindViewImpl(key = sourceLocation(), inputs = null, callback = callback)
}

inline fun <T : View> ComponentContext<T>.onUnbindView(
    vararg inputs: Any?,
    noinline callback: (T) -> Unit
) {
    checkIsComposing()
    onUnbindViewImpl(key = sourceLocation(), inputs = inputs, callback = callback)
}

@PublishedApi
internal fun <T : View> ComponentContext<T>.onUnbindViewImpl(
    key: Any,
    inputs: Array<out Any?>?,
    callback: (T) -> Unit
) {
    checkIsComposing()
    key(key) {
        val component = currentComponent<T>()
        val removeCallbackHolder = memo { RemoveCallbackHolder(null) }
        if (inputs != null) {
            composer.remember(*inputs) {
                removeCallbackHolder.removeCallback?.invoke()
                removeCallbackHolder.removeCallback = component.onUnbindView(callback)
            }
        } else {
            composer.changed(callback)
            removeCallbackHolder.removeCallback?.invoke()
            removeCallbackHolder.removeCallback = component.onUnbindView(callback)
        }
        onDispose { removeCallbackHolder.removeCallback?.invoke() }
    }
}

inline fun <T : View, V> ComponentContext<T>.set(value: V, crossinline block: T.(V) -> Unit) {
    checkIsComposing()
    currentViewUpdater<T>().set(value) { block(it) }
}

inline fun <T : View> ComponentContext<T>.init(crossinline block: T.() -> Unit) {
    checkIsComposing()
    currentViewUpdater<T>().set(Unit) { block() }
}

inline fun <T : View> ComponentContext<T>.update(crossinline block: T.() -> Unit) {
    checkIsComposing()
    currentViewUpdater<T>().set(Any()) { block() }
}