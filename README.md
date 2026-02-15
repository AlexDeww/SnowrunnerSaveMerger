# SnowRunner Save Merger

A utility for synchronizing co-op progress in SnowRunner.
Allows guests to transfer completed contracts, discovered watchtowers, 
and found upgrades from the host’s save into their own personal save.

### Why is this needed?

In SnowRunner, world progress (completed contracts, revealed maps) is saved only for the host.
If you played in co-op on a friend’s session and want those tasks to be marked as completed in your single-player game, 
this tool allows you to merge that data.

### What will be transferred

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

### How to transfer progress (Instructions)

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
 - `--origin` - host save before the session (baseline).
 - `--source` - host save after the session (result).
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
