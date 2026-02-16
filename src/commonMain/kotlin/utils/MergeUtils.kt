package org.home.utils

import kotlin.jvm.JvmName
import kotlin.reflect.KProperty1

typealias Prop<O, T> = KProperty1<O, T>
typealias PropSet<O, T> = Prop<O, Set<T>>
typealias PropMap<O, T> = Prop<O, Map<String, T>>

interface MergeObjectHelper<O> {

    companion object {
        fun <O> create(baseObj: O, originObj: O, sourceObj: O) = object : MergeObjectHelper<O> {
            override val baseObj: O = baseObj
            override val originObj: O = originObj
            override val sourceObj: O = sourceObj
        }
    }

    val baseObj: O
    val originObj: O
    val sourceObj: O

    operator fun invoke(block: MergeObjectHelper<O>.() -> O): O = block()

}

@JvmName("doMerge")
context(helper: MergeObjectHelper<O>)
fun <O, T> Prop<O, T>.doMerge(block: (b: T, o: T, s: T) -> T): T = with(helper) {
    block(get(baseObj), get(originObj), get(sourceObj))
}

@JvmName("mergeSet")
context(_: MergeObjectHelper<O>)
fun <O, T> PropSet<O, T>.merge(
    asMonotonic: Boolean = false,
    keyProc: KeyProc<T> = ::defaultKeyProc,
) = doMerge { b, o, s ->
    b.merge(o, s, asMonotonic, keyProc)
}

@JvmName("mergeMap")
context(_: MergeObjectHelper<O>)
fun <O, T> PropMap<O, T>.merge(
    asMonotonic: Boolean = false,
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged,
    resolveValue: ResolveValueProc<T> = ::defaultResolveValue
) = doMerge { b, o, s ->
    b.merge(o, s, asMonotonic, areValueChanged, resolveValue)
}

@JvmName("mergeMapMaxStrategy")
context(_: MergeObjectHelper<O>)
fun <O, T : Comparable<T>> PropMap<O, T>.mergeMaxStrategy(
    asMonotonic: Boolean = false
) = doMerge { b, o, s ->
    b.mergeMaxStrategy(o, s, asMonotonic)
}

fun <T> Map<String, T>.merge(
    origin: Map<String, T>,
    source: Map<String, T>,
    asMonotonic: Boolean = false,
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged,
    resolveValue: ResolveValueProc<T> = ::defaultResolveValue
): Map<String, T> {
    val diff = origin.diff(source, !asMonotonic, areValueChanged)
    return this.applyDiff(diff, resolveValue)
}

fun <T : Comparable<T>> Map<String, T>.mergeMaxStrategy(
    origin: Map<String, T>,
    source: Map<String, T>,
    asMonotonic: Boolean = true,
): Map<String, T> = merge(
    origin = origin,
    source = source,
    asMonotonic = asMonotonic,
    areValueChanged = { o, n -> n > o },
    resolveValue = { o, n -> if (n > o) n else o }
)

fun <T> Set<T>.merge(
    origin: Set<T>,
    source: Set<T>,
    asMonotonic: Boolean = false,
    keyProc: KeyProc<T> = ::defaultKeyProc,
): Set<T> {
    val diff = origin.diff(source, !asMonotonic, keyProc)
    return this.applyDiffTo(mutableSetOf(), diff)
}
