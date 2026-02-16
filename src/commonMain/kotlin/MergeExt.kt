package org.home

import org.home.model.PersistentProfileData
import org.home.model.SslValue
import org.home.utils.*
import kotlin.jvm.JvmName
import org.home.utils.DiffOperation as DiffOp

private typealias SslValueMergeHelper = MergeObjectHelper<SslValue>
private typealias PersistentProfileDataMergeHelper = MergeObjectHelper<PersistentProfileData>

private typealias SslValueProp<T> = Prop<SslValue, T>
private typealias PersistentProfileDataProp<T> = Prop<PersistentProfileData, T>

@JvmName("mergeSslValueWatchPointsData")
context(_: SslValueMergeHelper)
fun SslValueProp<SslValue.WatchPointsData>.merge() = doMerge { b, o, s ->
    val data = b.data.merge(o.data, s.data, asMonotonic = true, resolveValue = ::resolveMaxStrategy)
    b.copy(data = data)
}

@JvmName("mergeSslValueLevelGarageStatuses")
context(_: SslValueMergeHelper)
fun SslValueProp<SslValue.LevelGarageStatuses>.merge() = doMerge { b, o, s ->
    val statuses = b.statuses.mergeMaxStrategy(o.statuses, s.statuses)
    SslValue.LevelGarageStatuses(statuses)
}

@JvmName("mergeSslValueObjectiveStates")
context(h: SslValueMergeHelper)
fun SslValueProp<SslValue.ObjectiveStates>.merge() = doMerge { b, o, s ->
    val completionDeletes = (h.sourceObj.finishedObjs - h.originObj.finishedObjs).map { DiffOp.Delete(it) }
    val diff = (o.states.diff(s.states) + completionDeletes).filter { it.key !in h.baseObj.finishedObjs }
    SslValue.ObjectiveStates(b.states.applyDiff(diff))
}

@JvmName("mergeSslValueViewedUnactivatedObjectives")
context(h: SslValueMergeHelper)
fun SslValueProp<SslValue.ViewedUnactivatedObjectives>.merge() = doMerge { b, o, s ->
    val activated = h.sourceObj.objectiveStates.states.keys - h.originObj.objectiveStates.states.keys
    val finished = h.sourceObj.finishedObjs - h.originObj.finishedObjs

    val completionDeletes = (activated + finished).map { DiffOp.Delete(it) }
    val diff = (o.list.diff(s.list) + completionDeletes)
        .filter { it.key !in h.baseObj.finishedObjs && it.key !in h.baseObj.objectiveStates.states }
    SslValue.ViewedUnactivatedObjectives(b.list.applyDiffTo(mutableSetOf(), diff))
}

@JvmName("mergeSslValueUpgradesGiverData")
context(_: SslValueMergeHelper)
fun SslValueProp<SslValue.UpgradesGiverData>.merge() = doMerge { b, o, s ->
    val data = b.data.merge(o.data, s.data, asMonotonic = true, resolveValue = ::resolveMaxStrategy)
    SslValue.UpgradesGiverData(data)
}

@JvmName("mergeSslValuePersistentProfileData")
context(_: SslValueMergeHelper)
fun SslValueProp<PersistentProfileData>.merge(): PersistentProfileData = doMerge { b, o, s ->
    val persistentProfileDataMergeHelper = MergeObjectHelper.create(b, o, s)
    persistentProfileDataMergeHelper {
        b.copy(
            discoveredTrucks = PersistentProfileData::discoveredTrucks.merge(),
            unlockedItemNames = PersistentProfileData::unlockedItemNames.merge(),
        )
    }
}

@JvmName("mergePersistentProfileDataDiscoveredTrucks")
context(_: PersistentProfileDataMergeHelper)
fun PersistentProfileDataProp<PersistentProfileData.DiscoveredTrucks>.merge() = doMerge { b, o, s ->
    val trucks = b.trucks.merge(
        origin = o.trucks,
        source = s.trucks,
        asMonotonic = true,
        resolveValue = { old, new -> if (new.current > old.current) new else old }
    )
    PersistentProfileData.DiscoveredTrucks(trucks)
}

@JvmName("mergePersistentProfileDataUnlockedItemNames")
context(_: PersistentProfileDataMergeHelper)
fun PersistentProfileDataProp<PersistentProfileData.UnlockedItemNames>.merge() = doMerge { b, o, s ->
    val names = b.names.merge(o.names, s.names, asMonotonic = true)
    PersistentProfileData.UnlockedItemNames(names)
}

private fun <T : Comparable<T>> resolveMaxStrategy(
    base: Map<String, T>,
    source: Map<String, T>,
): Map<String, T> = base.mergeMaxStrategy(
    origin = base,
    source = source,
    asMonotonic = true
)
