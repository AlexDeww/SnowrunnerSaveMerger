package org.home.utils

import org.home.utils.DiffOperation.*

typealias DiffResult<T> = List<DiffOperation<T>>
typealias AreValueChangedProc<T> = (oldValue: T, newValue: T) -> Boolean

sealed class DiffOperation<T> {
    abstract val key: String

    data class Delete<T>(override val key: String) : DiffOperation<T>()
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
    keyProc: (T) -> String = { it.toString() },
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged
): DiffResult<T> = this
    .associateBy(keyProc)
    .diff(
        newValue = newValue.associateBy(keyProc),
        allowDelete = allowDelete,
        areValueChanged = areValueChanged
    )

fun <T> Map<String, T>.applyDiff(
    diffResult: DiffResult<T>,
    resolveValue: (oldValue: T, newValue: T) -> T = { _, new -> new }
): Map<String, T> {
    return if (diffResult.isEmpty()) this
    else applyDiffInternal(toMutableMap(), diffResult, resolveValue)
}

fun <T> Collection<T>.applyDiff(
    diffResult: DiffResult<T>,
    keyProc: (T) -> String = { it.toString() },
    resolveValue: (oldValue: T, newValue: T) -> T = { _, new -> new }
): List<T> = applyDiffTo(
    destination = mutableListOf(),
    diffResult = diffResult,
    keyProc = keyProc,
    resolveValue = resolveValue
)

fun <T, R : MutableCollection<T>> Collection<T>.applyDiffTo(
    destination: R,
    diffResult: DiffResult<T>,
    keyProc: (T) -> String = { it.toString() },
    resolveValue: (oldValue: T, newValue: T) -> T = { _, new -> new }
): R {
    if (diffResult.isEmpty()) destination.addAll(this)
    else this
        .associateByTo(linkedMapOf(), keyProc)
        .let { applyDiffInternal(it, diffResult, resolveValue) }
        .let { destination.addAll(it.values) }

    return destination
}

fun <T> defaultAreValueChanged(old: T, new: T): Boolean = old != new

private fun <T, R : MutableMap<String, T>> applyDiffInternal(
    target: R,
    diffResult: DiffResult<T>,
    resolveValue: (oldValue: T, newValue: T) -> T
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
