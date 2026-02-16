package org.home.utils

import org.home.utils.DiffOperation.*

typealias DiffResult<T> = List<DiffOperation<T>>
typealias AreValueChangedProc<T> = (oldValue: T, newValue: T) -> Boolean
typealias ResolveValueProc<T> = (oldValue: T, newValue: T) -> T
typealias KeyProc<T> = (idx: Int, item: T) -> String

sealed class DiffOperation<out T> {
    abstract val key: String

    data class Delete(override val key: String) : DiffOperation<Nothing>()
    data class Insert<T>(override val key: String, val value: T) : DiffOperation<T>()
    data class Change<T>(override val key: String, val value: T) : DiffOperation<T>()
}

fun <T> Map<String, T>.diff(
    newValue: Map<String, T>,
    allowDelete: Boolean = true,
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged
): DiffResult<T> {
    val oldKeys = this.keys
    val newKeys = newValue.keys

    return buildList {
        if (allowDelete) (oldKeys - newKeys).forEach { add(Delete(it)) }
        (newKeys - oldKeys).forEach { add(Insert(it, newValue.getValue(it))) }
        (oldKeys intersect newKeys)
            .filter { areValueChanged(this@diff.getValue(it), newValue.getValue(it)) }
            .forEach { add(Change(it, newValue.getValue(it))) }
    }
}

fun <T> Collection<T>.diff(
    newValue: Collection<T>,
    allowDelete: Boolean = true,
    keyProc: KeyProc<T> = ::defaultKeyProc,
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged
): DiffResult<T> = this
    .withIndex()
    .associateBy(
        keySelector = { keyProc(it.index, it.value) },
        valueTransform = { it.value }
    )
    .diff(
        newValue = newValue
            .withIndex()
            .associateBy(
                keySelector = { keyProc(it.index, it.value) },
                valueTransform = { it.value }
            ),
        allowDelete = allowDelete,
        areValueChanged = areValueChanged
    )

fun <T> Map<String, T>.applyDiff(
    diffResult: DiffResult<T>,
    resolveValue: ResolveValueProc<T> = ::defaultResolveValue
): Map<String, T> {
    return if (diffResult.isEmpty()) this
    else applyDiffInternal(toMutableMap(), diffResult, resolveValue)
}

fun <T> Collection<T>.applyDiff(
    diffResult: DiffResult<T>,
    keyProc: KeyProc<T> = ::defaultKeyProc,
    resolveValue: ResolveValueProc<T> = ::defaultResolveValue
): List<T> = applyDiffTo(
    destination = mutableListOf(),
    diffResult = diffResult,
    keyProc = keyProc,
    resolveValue = resolveValue
)

fun <T, R : MutableCollection<T>> Collection<T>.applyDiffTo(
    destination: R,
    diffResult: DiffResult<T>,
    keyProc: KeyProc<T> = ::defaultKeyProc,
    resolveValue: ResolveValueProc<T> = ::defaultResolveValue
): R {
    if (diffResult.isEmpty()) destination.addAll(this)
    else this
        .withIndex()
        .associateByTo(
            destination = linkedMapOf(),
            keySelector = { keyProc(it.index, it.value) },
            valueTransform = { it.value }
        )
        .let { applyDiffInternal(it, diffResult, resolveValue) }
        .let { destination.addAll(it.values) }

    return destination
}

fun <T> defaultAreValueChanged(old: T, new: T): Boolean = old != new

fun <T> defaultResolveValue(old: T, new: T): T = new

fun <T> defaultKeyProc(idx: Int, item: T): String = item.toString()

private fun <T, R : MutableMap<String, T>> applyDiffInternal(
    target: R,
    diffResult: DiffResult<T>,
    resolveValue: ResolveValueProc<T>
): R {
    diffResult.forEach { op ->
        when (op) {
            is Delete -> target.remove(op.key)
            is Insert -> target.compute(op.key) { _, v -> v?.let { resolveValue(it, op.value) } ?: op.value }
            is Change -> target.compute(op.key) { _, v -> v?.let { resolveValue(it, op.value) } ?: op.value }
        }
    }

    return target
}

private fun <T, R : MutableMap<String, T>> R.compute(key: String, block: (key: String, value: T?) -> T) {
    this[key] = block(key, this[key])
}
