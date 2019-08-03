package com.ivianuu.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.Applier
import androidx.compose.ApplyAdapter
import androidx.compose.Composer
import androidx.compose.Effect
import androidx.compose.EffectsDsl
import androidx.compose.FrameManager
import androidx.compose.Recomposer
import androidx.compose.SlotTable
import androidx.compose.ViewUpdater
import androidx.compose.ambient
import java.util.*

private fun invalidNode(node: Any): Nothing =
    error("Unsupported node type ${node.javaClass.simpleName}")

class ViewApplyAdapter(root: Any) : ApplyAdapter<Any> {

    val stack = Stack<Any>()
    val current: Any get() = _current
    private var _current: Any = root

    private sealed class Op {
        data class Insert(val index: Int, val instance: Any) : Op()
        data class Move(val from: Int, val to: Int, val count: Int) : Op()
        data class Remove(val index: Int, val count: Int) : Op()
    }
    private var ops = mutableListOf<Op>()
    private val opsStack = Stack<MutableList<Op>>()

    override fun Any.start(instance: Any) {
        println("start $instance")
        stack.push(_current)
        _current = instance

        opsStack += ops
        ops = mutableListOf()
    }
    override fun Any.insertAt(index: Int, instance: Any) {
        ops.add(Op.Insert(index, instance))
    }

    override fun Any.move(from: Int, to: Int, count: Int) {
        ops.add(Op.Move(from, to, count))
    }

    override fun Any.removeAt(index: Int, count: Int) {
        ops.add(Op.Remove(index, count))
    }

    override fun Any.end(instance: Any, parent: Any) {
        _current = stack.pop()

        if (ops.isNotEmpty()) {
            println("ops $ops")

            val container = when (parent) {
                is ViewGroup -> parent
                is Compose.Root -> parent.container
                else -> invalidNode(this)
            }

            val viewManager = container.getViewManager()

            val oldViews = viewManager.views
            val newViews = oldViews.toMutableList()

            var insertCount = 0
            var removeCount = 0

            ops.forEach { op ->
                when (op) {
                    is Op.Insert -> {
                        newViews.add(op.index, op.instance as View)
                        insertCount++
                    }
                    is Op.Move -> {
                        if (op.from > op.to) {
                            var currentFrom = op.from
                            var currentTo = op.to
                            repeat(op.count) {
                                Collections.swap(newViews, currentFrom, currentTo)
                                currentFrom++
                                currentTo++
                            }
                        } else {
                            repeat(op.count) {
                                Collections.swap(newViews, op.from, op.to - 1)
                            }
                        }
                    }
                    is Op.Remove -> {
                        for (i in op.index + op.count - 1 downTo op.index) {
                            newViews.removeAt(i)
                            removeCount++
                        }
                    }
                }
            }

            viewManager.setViews(newViews, insertCount >= removeCount)
        }

        ops = opsStack.pop()
    }
}

class ViewComposer(
    val root: Any,
    val applyAdapter: ViewApplyAdapter = ViewApplyAdapter(root),
    recomposer: Recomposer
) : Composer<Any>(
    SlotTable(),
    Applier(root, applyAdapter), recomposer
) {

    init {
        FrameManager.ensureStarted()
    }
}

@Suppress("UNCHECKED_CAST")
@EffectsDsl
class ViewComposition(val composer: ViewComposer) {

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun <V> Effect<V>.unaryPlus(): V =
        resolve(this@ViewComposition.composer, sourceLocation().hashCode())

    inline fun <T : View> emit(
        key: Any,
        crossinline ctor: (ViewGroup) -> T,
        update: ViewUpdater<T>.() -> Unit
    ) = with(composer) {
        startNode(key)
        println("emit $key current ${applyAdapter.current} stack ${applyAdapter.stack}")
        val node = if (inserting) {
            val container =
                when (val parent = applyAdapter.stack.lastOrNull() ?: applyAdapter.current) {
                is ViewGroup -> parent
                else -> (parent as Compose.Root).container
            }
            ctor(container).also { emitNode(it) }
        } else {
            useNode() as T
        }
        ViewUpdater(this, node).update()

        node.inTransition = +ambient(InTransitionAmbient)
        node.outTransition = +ambient(OutTransitionAmbient)

        endNode()
    }

    inline fun <T : ViewGroup> emit(
        key: Any,
        crossinline ctor: (ViewGroup) -> T,
        update: ViewUpdater<T>.() -> Unit,
        children: () -> Unit
    ) = with(composer) {
        startNode(key)
        println("emit $key current ${applyAdapter.current} stack ${applyAdapter.stack}")
        val node = if (inserting) {
            val container =
                when (val parent = applyAdapter.stack.lastOrNull() ?: applyAdapter.current) {
                is ViewGroup -> parent
                else -> (parent as Compose.Root).container
            }
            ctor(container).also { emitNode(it) }
        } else {
            useNode() as T
        }
        ViewUpdater(this, node).update()

        node.inTransition = +ambient(InTransitionAmbient)
        node.outTransition = +ambient(OutTransitionAmbient)

        children()
        endNode()
    }

}