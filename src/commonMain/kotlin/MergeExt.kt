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
context(helper: SslValueMergeHelper)
fun SslValueProp<SslValue.WatchPointsData>.merge() = with(helper) {
    doMerge { b, o, s ->
        val diff = o.data.diff(s.data, allowDelete = false)
        val data = b.data.applyDiff(diff, ::resolveMaxStrategy)
        b.copy(data = data)
    }
}

@JvmName("mergeSslValueLevelGarageStatuses")
context(helper: SslValueMergeHelper)
fun SslValueProp<SslValue.LevelGarageStatuses>.merge() = with(helper) {
    doMerge { b, o, s ->
        val statuses = b.statuses.mergeMaxStrategy(o.statuses, s.statuses)
        SslValue.LevelGarageStatuses(statuses)
    }
}

@JvmName("mergeSslValueObjectiveStates")
context(helper: SslValueMergeHelper)
fun SslValueProp<SslValue.ObjectiveStates>.merge() = with(helper) {
    doMerge { b, o, s ->
        val completionDeletes = (sourceObj.finishedObjs - originObj.finishedObjs).map { DiffOp.Delete(it) }
        val diff = (o.states.diff(s.states) + completionDeletes).filter { it.key !in baseObj.finishedObjs }
        SslValue.ObjectiveStates(b.states.applyDiff(diff))
    }
}

@JvmName("mergeSslValueViewedUnactivatedObjectives")
context(helper: SslValueMergeHelper)
fun SslValueProp<SslValue.ViewedUnactivatedObjectives>.merge() = with(helper) {
    doMerge { b, o, s ->
        val activated = sourceObj.objectiveStates.states.keys - originObj.objectiveStates.states.keys
        val finished = sourceObj.finishedObjs - originObj.finishedObjs

        val completionDeletes = (activated + finished).map { DiffOp.Delete(it) }
        val diff = (o.list.diff(s.list) + completionDeletes)
            .filter { it.key !in baseObj.finishedObjs && it.key !in baseObj.objectiveStates.states }
        SslValue.ViewedUnactivatedObjectives(b.list.applyDiffTo(mutableSetOf(), diff))
    }
}

@JvmName("mergeSslValueUpgradesGiverData")
context(helper: SslValueMergeHelper)
fun SslValueProp<SslValue.UpgradesGiverData>.merge() = with(helper) {
    doMerge { b, o, s ->
        val diff = o.data.diff(s.data, allowDelete = false)
        val data = b.data.applyDiff(diff, ::resolveMaxStrategy)
        SslValue.UpgradesGiverData(data)
    }
}

@JvmName("mergeSslValuePersistentProfileData")
context(helper: SslValueMergeHelper)
fun SslValueProp<PersistentProfileData>.merge(): PersistentProfileData = with(helper) {
    doMerge { b, o, s ->
        val persistentProfileDataMergeHelper = MergeObjectHelper.create(b, o, s)
        persistentProfileDataMergeHelper {
            b.copy(
                discoveredTrucks = PersistentProfileData::discoveredTrucks.merge(),
                ownedTrucks = PersistentProfileData::ownedTrucks.merge()
            )
        }
    }
}

@JvmName("mergePersistentProfileDataDiscoveredTrucks")
context(helper: PersistentProfileDataMergeHelper)
fun PersistentProfileDataProp<PersistentProfileData.DiscoveredTrucks>.merge() = with(helper) {
    doMerge { b, o, s ->
        val diff = o.trucks.diff(s.trucks, allowDelete = false)
        val trucks = b.trucks.applyDiff(diff) { o, n -> if (n.current > o.current) n else o }
        PersistentProfileData.DiscoveredTrucks(trucks)
    }
}

@JvmName("mergePersistentProfileDataOwnedTrucks")
context(helper: PersistentProfileDataMergeHelper)
fun PersistentProfileDataProp<PersistentProfileData.OwnedTrucks>.merge() = with(helper) {
    doMerge { b, o, s ->
        val diff = o.trucks.diff(s.trucks, allowDelete = false)
        PersistentProfileData.OwnedTrucks(b.trucks.applyDiff(diff))
    }
}

private fun <T : Comparable<T>> resolveMaxStrategy(
    base: Map<String, T>,
    source: Map<String, T>,
): Map<String, T> = base.mergeMaxStrategy(
    origin = base,
    source = source,
    isMonotonic = true
)
