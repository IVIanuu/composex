package com.ivianuu.compose

import androidx.compose.Applier
import androidx.compose.ApplyAdapter
import androidx.compose.ComposeAccessor
import androidx.compose.Composer
import androidx.compose.Effect
import androidx.compose.EffectsDsl
import androidx.compose.FrameManager
import androidx.compose.Recomposer
import androidx.compose.SlotTable
import androidx.compose.ambient
import java.util.*

class ViewApplyAdapter(private val root: Component<*>) : ApplyAdapter<Component<*>> {

    private var current: Component<*> = root
    private val currentStack = Stack<Component<*>>()

    private val childrenByParent =
        mutableMapOf<Component<*>, MutableList<Component<*>>>()

    private fun MutableMap<Component<*>, MutableList<Component<*>>>.getOrPut(key: Component<*>) =
        getOrPut(key) {
            mutableListOf<Component<*>>().apply {
                addAll(key.children)
            }
        }

    override fun Component<*>.start(instance: Component<*>) {
        currentStack.push(current)
        current = this
    }

    override fun Component<*>.insertAt(index: Int, instance: Component<*>) {
        childrenByParent.getOrPut(this).add(index, instance)
    }

    override fun Component<*>.move(from: Int, to: Int, count: Int) {
        val children = childrenByParent.getOrPut(this)
        repeat(count) {
            children.add(to, children.removeAt(from))
        }
    }

    override fun Component<*>.removeAt(index: Int, count: Int) {
        val children = childrenByParent.getOrPut(this)
        (index until index + count).forEach {
            children.removeAt(it)
        }
    }

    override fun Component<*>.end(instance: Component<*>, parent: Component<*>) {
        if (this != current && current == instance) {
            instance.updateChildren(childrenByParent.getOrPut(instance))
            current = currentStack.pop()
            childrenByParent.remove(instance)

            if (current == root) {
                root.updateChildren(childrenByParent.getOrPut(root))
                childrenByParent.remove(root)
            }
        }
    }

}

class ViewComposer(
    val root: Component<*>,
    applyAdapter: ViewApplyAdapter = ViewApplyAdapter(root),
    recomposer: Recomposer
) : Composer<Component<*>>(
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
    inline operator fun <V> Effect<V>.unaryPlus(): V {
        check(ComposeAccessor.isComposing(this@ViewComposition.composer)) {
            "Can only use effects while composing"
        }
        return resolve(this@ViewComposition.composer, sourceLocation().hashCode())
    }

    fun <T : Component<*>> emit(
        key: Any,
        ctor: () -> T,
        update: (T.() -> Unit)? = null
    ) = with(composer) {
        startNode(key)
        log { "emit $key inserting ? $inserting" }
        val node = if (inserting) {
            ctor().also { emitNode(it) }
        } else {
            useNode() as T
        }

        node._key = key

        // todo remove
        node.inChangeHandler = +ambient(InChangeHandlerAmbient)
        node.outChangeHandler = +ambient(OutChangeHandlerAmbient)
        node.wasPush = +ambient(TransitionHintsAmbient)

        update?.let { node.it() }
        node.update()

        endNode()
    }

    inline fun group(noinline children: ViewComposition.() -> Unit) =
        group(sourceLocation(), children)

    fun group(
        key: Any,
        children: ViewComposition.() -> Unit
    ) = with(composer) {
        startGroup(key)
        children()
        endGroup()
    }

}