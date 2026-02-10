package org.home

import org.home.model.PersistentProfileData
import org.home.model.SslValue
import org.home.utils.*

@JvmName("mergeSslValueWatchPointsData")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.WatchPointsData>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.data.diff(b.data)
        val data = t.data.applyDiff(diff) { o, n -> o.mergeMaxStrategy(o, n) }
        t.copy(data = data)
    }
}

@JvmName("mergeSslValueLevelGarageStatuses")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.LevelGarageStatuses>.merge() = with(helper) {
    doMerge { t, a, b ->
        SslValue.LevelGarageStatuses(t.statues.mergeMaxStrategy(a.statues, b.statues))
    }
}

@JvmName("mergeSslValueObjectiveStates")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.ObjectiveStates>.merge() = with(helper) {
    doMerge { t, a, b ->
        val finishedObjs = targetObject.finishedObjs
        val diff = a.state.diff(b.state).filter { it.key !in finishedObjs }
        SslValue.ObjectiveStates(t.state.applyDiff(diff))
    }
}

@JvmName("mergeSslValueUpgradesGiverData")
context(helper: MergeObjectHelper<SslValue>)
fun Prop<SslValue, SslValue.UpgradesGiverData>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.data.diff(b.data)
        val data = t.data.applyDiff(diff) { o, n -> o.mergeMaxStrategy(o, n) }
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
        val diff = a.trucks.diff(b.trucks)
        val trucks = t.trucks.applyDiff(diff) { o, n -> if (n.current > o.current) n else o }
        PersistentProfileData.DiscoveredTrucks(trucks)
    }
}

@JvmName("mergePersistentProfileDataOwnedTrucks")
context(helper: MergeObjectHelper<PersistentProfileData>)
fun Prop<PersistentProfileData, PersistentProfileData.OwnedTrucks>.merge() = with(helper) {
    doMerge { t, a, b ->
        val diff = a.trucks.diff(b.trucks)
        PersistentProfileData.OwnedTrucks(t.trucks.applyDiff(diff))
    }
}
