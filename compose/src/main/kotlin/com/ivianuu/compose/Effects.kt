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

import androidx.compose.Ambient
import androidx.compose.CommitScope
import androidx.compose.ComposeAccessor
import androidx.compose.Effect

inline fun <T> ComponentComposition.key(vararg inputs: Any?, noinline block: Effect<T>.() -> T) =
    androidx.compose.key(inputs = *inputs, block = block).resolve(composer, sourceLocation())

inline fun <T> ComponentComposition.memo(vararg inputs: Any?, noinline calculation: () -> T) =
    androidx.compose.memo(inputs = *inputs, calculation = calculation).resolve(
        composer,
        sourceLocation()
    )

inline fun ComponentComposition.onActive(
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onActive(callback = callback).resolve(composer, sourceLocation())

inline fun ComponentComposition.onDispose(
    noinline callback: () -> Unit
) = androidx.compose.onDispose(callback = callback).resolve(composer, sourceLocation())

inline fun ComponentComposition.onCommit(
    vararg inputs: Any?,
    noinline callback: CommitScope.() -> Unit
) =
    androidx.compose.onCommit(inputs = *inputs, callback = callback).resolve(
        composer,
        sourceLocation()
    )

inline fun ComponentComposition.onPreCommit(
    vararg inputs: Any?,
    noinline callback: CommitScope.() -> Unit
) =
    androidx.compose.onPreCommit(inputs = *inputs, callback = callback).resolve(
        composer,
        sourceLocation()
    )

inline fun <T> ComponentComposition.state(vararg inputs: Any?, noinline init: () -> T) =
    androidx.compose.stateFor(inputs = *inputs, init = init).resolve(composer, sourceLocation())

inline fun <T> ComponentComposition.model(vararg inputs: Any?, noinline init: () -> T) =
    androidx.compose.modelFor(inputs = *inputs, init = init).resolve(composer, sourceLocation())

inline fun <T> ComponentComposition.ambient(key: Ambient<T>) =
    androidx.compose.ambient(key = key).resolve(composer, sourceLocation())

inline val ComponentComposition.invalidate: () -> Unit
    get() = androidx.compose.invalidate.resolve(composer, sourceLocation())

@PublishedApi
internal fun <T> Effect<T>.resolve(composer: ComponentComposer, key: Any): T {
    check(ComposeAccessor.isComposing(composer)) {
        "Can only use effects while composing"
    }

    return resolve(composer, key.hashCode())
}