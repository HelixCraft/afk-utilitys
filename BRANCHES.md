# Branch Compatibility Guide

This repository uses a branching strategy to support multiple Minecraft versions. Below is the list of branches and their compatible versions.

| Branch Name       | Compatible Minecraft Versions | Notes                                      |
| ----------------- | ----------------------------- | ------------------------------------------ |
| `1.20-1.20.1`     | 1.20, 1.20.1                  | Stable 1.20 API.                           |
| `1.20.2-1.20.2`   | 1.20.2                        | GUI and Packet signature changes.          |
| `1.20.3-1.20.4`   | 1.20.3, 1.20.4                | ObjectSelectionList changes.               |
| `1.20.5-1.20.6`   | 1.20.5, 1.20.6                | Data Components & TransferState changes.   |

## Usage
To build for a specific version, checkout the corresponding branch:

```bash
git checkout 1.20-1.20.1
./gradlew build
```
