package com.ivianuu.compose.sample

import android.animation.ValueAnimator
import android.view.View
import androidx.compose.onActive
import androidx.compose.state
import com.ivianuu.compose.View
import com.ivianuu.compose.ViewComposition
import com.ivianuu.compose.layoutRes
import com.ivianuu.compose.sample.common.Route
import kotlinx.android.synthetic.main.animation.view.*

fun ViewComposition.Animation() = Route {
    var value by +state { 0f }
    +onActive {
        val animation = ValueAnimator()
        animation.setFloatValues(0f, 1f)
        animation.repeatMode = ValueAnimator.REVERSE
        animation.repeatCount = ValueAnimator.INFINITE

        animation.addUpdateListener {
            value = it.animatedFraction
        }

        animation.start()

        onDispose { animation.cancel() }
    }

    View<View> {
        layoutRes(R.layout.animation)
        bindView {
            animation_view.scaleX = value
            animation_view.scaleY = value
        }
    }
}