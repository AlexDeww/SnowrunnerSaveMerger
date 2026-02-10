package org.home.utils

import kotlin.reflect.KProperty1

typealias Prop<O, T> = KProperty1<O, T>
typealias PropSet<O, T> = Prop<O, Set<T>>
typealias PropMap<O, T> = Prop<O, Map<String, T>>

interface MergeObjectHelper<O> {

    companion object {
        fun <O> create(targetObj: O, beginObj: O, endObj: O) = object : MergeObjectHelper<O> {
            override val targetObject: O = targetObj
            override val beginObject: O = beginObj
            override val endObject: O = endObj
        }
    }

    val targetObject: O
    val beginObject: O
    val endObject: O

}

operator fun <O> MergeObjectHelper<O>.invoke(block: MergeObjectHelper<O>.() -> O): O = block()

@JvmName("doMerge")
context(helper: MergeObjectHelper<O>)
fun <O, T> Prop<O, T>.doMerge(block: (t: T, a: T, b: T) -> T): T = with(helper) {
    block(get(targetObject), get(beginObject), get(endObject))
}

@JvmName("mergeSet")
context(helper: MergeObjectHelper<O>)
fun <O, T> PropSet<O, T>.merge() = with(helper) {
    doMerge(Set<T>::merge)
}

@JvmName("mergeMap")
context(helper: MergeObjectHelper<O>)
fun <O, T> PropMap<O, T>.merge(
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged
) = with(helper) {
    doMerge { t, a, b -> t.merge(a, b, areValueChanged) }
}

@JvmName("mergeMapMaxStrategy")
context(helper: MergeObjectHelper<O>)
fun <O, T : Comparable<T>> PropMap<O, T>.mergeMaxStrategy() = with(helper) {
    doMerge(Map<String, T>::mergeMaxStrategy)
}

fun <T> Map<String, T>.merge(
    begin: Map<String, T>,
    end: Map<String, T>,
    areValueChanged: AreValueChangedProc<T> = ::defaultAreValueChanged
): Map<String, T> {
    val diff = begin.diff(end, areValueChanged = areValueChanged)
    return this.applyDiff(diff)
}

fun <T : Comparable<T>> Map<String, T>.mergeMaxStrategy(
    begin: Map<String, T>,
    end: Map<String, T>,
): Map<String, T> = merge(
    begin = begin,
    end = end,
    areValueChanged = { o, n -> n > o }
)

fun <T> Set<T>.merge(begin: Set<T>, end: Set<T>): Set<T> {
    val diff = begin.diff(end)
    return this.applyDiffTo(mutableSetOf(), diff)
}
