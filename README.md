# SnowRunner Save Merger

A utility for synchronizing co-op progress in SnowRunner.
Allows guests to merge completed contracts, discovered watchtowers, 
and found upgrades from the host’s save into their own personal save.

### Why is this needed?

In SnowRunner, world progress (completed contracts, revealed maps) is saved only for the host.
If you played in co-op on a friend’s session and want those tasks to be marked as completed in your single-player game, 
this tool allows you to merge that data.

### What will be merged

  - Completed objectives (`finishedObjs`)
  - Active objectives (`objectiveStates`)
  - Discovered objectives (`discoveredObjectives`)
  - Discovered but not activated objectives (`viewedUnactivatedObjectives`)
  - Found upgrades (`upgradesGiverData`)
  - Discovered/unlocked garages (`levelGarageStatuses`)
  - Discovered/unlocked watchtowers (`watchPointsData`)
  - Visited maps (`visitedLevels`)
  - Number of discovered trucks (`discoveredTrucks`)
  - Discovered trucks (`ownedTrucks`)

### How merge works

The merger performs a safe, non-destructive merge of world progress.
It does **not overwrite your `base` save**.

Instead, it only adds the progress that was actually gained during the co-op session, 
calculated as the difference between the host’s `origin` and `source`.

This guarantees that:
 - Your existing progress in `base` **will never be lost**
 - Already completed contracts **will not become active again**
 - Previously unlocked watchtowers **will not be closed**
 - Your discovered maps, garages, trucks, and upgrades **remain intact**
 - Only **newly completed / discovered** content from the co-op session is applied

In other words, the tool performs a **progress union**, not a replacement.

If something is completed in either:
 - your personal save (`base`), or
 - the host session result (`source`),

it will be marked as completed in the final merged save.

### How to merge progress (Instructions)

For the merge to work correctly, the host must capture the world state before and after the co-op session:

1. Host: Copy the save file before starting the session → `origin`.
2. Host: Take the updated save file after the session → `source`.
3. Host: Send both files (`origin` and `source`) to the guest.
4. Guest: Use the utility to merge them with your own save → `base`.

### Usage
Run from the command line:

```bash
SnowrunnerSaveMerger.exe --base "CompleteSave.cfg" --origin "host/start/CompleteSave.cfg" --source "host/end/CompleteSave.cfg" --backup
```

Parameters:
 - `--base` - your personal save file to which progress will be added.
 - `--origin` - host save before the session (optional).
 - `--source` - host save after the session.
 - `--backup` - creates a backup of your file before merging.

---

### Building the project

* Windows EXE:
```bash
./gradlew windowsBinaries
```
Output: `build/bin/windows/releaseExecutable/`

* JAR:
```bash
./gradlew shadowJar
```
Output: `build/libs/`
