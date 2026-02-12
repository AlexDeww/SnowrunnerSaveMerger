package org.home

import org.home.model.PersistentProfileData
import org.home.model.SslValue
import org.home.utils.*

@JvmName("mergeSslValueWatchPointsData")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.WatchPointsData>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.data.diff(b.data, allowDelete = false)
        val data = t.data.applyDiff(diff, ::mergeMaxStrategy)
        t.copy(data = data)
    }
}

@JvmName("mergeSslValueLevelGarageStatuses")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.LevelGarageStatuses>.merge() = with(helper) {
    doMerge { t, a, b ->
        SslValue.LevelGarageStatuses(t.statuses.mergeMaxStrategy(a.statuses, b.statuses))
    }
}

@JvmName("mergeSslValueObjectiveStates")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.ObjectiveStates>.merge() = with(helper) {
    doMerge { t, a, b ->
        // TODO: Add 2Way merge logic
        val finishedObjs = targetObject.finishedObjs
        val diff = a.states.diff(b.states).filter { it.key !in finishedObjs }
        SslValue.ObjectiveStates(t.states.applyDiff(diff))
    }
}

@JvmName("mergeSslValueUpgradesGiverData")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.UpgradesGiverData>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.data.diff(b.data, allowDelete = false)
        val data = t.data.applyDiff(diff, ::mergeMaxStrategy)
        SslValue.UpgradesGiverData(data)
    }
}

@JvmName("mergeSslValuePersistentProfileData")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, PersistentProfileData>.merge(): PersistentProfileData = with(helper) {
    doMerge { t, a, b ->
        val persistentProfileDataMergeHelper = MergeObjectHelper.create(t, a, b)
        persistentProfileDataMergeHelper {
            t.copy(
                discoveredTrucks = PersistentProfileData::discoveredTrucks.merge(),
                ownedTrucks = PersistentProfileData::ownedTrucks.merge()
            )
        }
    }
}

@JvmName("mergePersistentProfileDataDiscoveredTrucks")
context(helper: MergeObjectHelper<PersistentProfileData>)
fun Prop<PersistentProfileData, PersistentProfileData.DiscoveredTrucks>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.trucks.diff(b.trucks, allowDelete = false)
        val trucks = t.trucks.applyDiff(diff) { o, n -> if (n.current > o.current) n else o }
        PersistentProfileData.DiscoveredTrucks(trucks)
    }
}

@JvmName("mergePersistentProfileDataOwnedTrucks")
context(helper: MergeObjectHelper<PersistentProfileData>)
fun Prop<PersistentProfileData, PersistentProfileData.OwnedTrucks>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.trucks.diff(b.trucks, allowDelete = false)
        PersistentProfileData.OwnedTrucks(t.trucks.applyDiff(diff))
    }
}

private fun <T : Comparable<T>> mergeMaxStrategy(
    a: Map<String, T>,
    b: Map<String, T>,
): Map<String, T> = a.mergeMaxStrategy(
    begin = a,
    end = b,
    isMonotonic = true
)
