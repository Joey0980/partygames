# PartyGames Minecraft Dart Mod

A simple, turn-based darts mini-game for Minecraft 1.21.1 using NeoForge. Throw custom darts at a DartBoard block, track your score on the in-game sidebar, and challenge friends or play solo! Built for the [Sprint.dev Minecraft Hackathon](https://www.sprint.dev/hackathons/minecrafthack).

---

## Overview

- **DartItem**: A throwable dart (mix of trident+arrow)  
- **DartBoardBlock**: Custom target block that calculates a 0–15 score based on hit position  
- **Scoreboard**: `/dart challenge <rounds> [player]` to start, `/dart stop` to end  
- **Solo or PvP**: Play by yourself or challenge a friend  

---

## Installation

1. **Clone from this template**  
   ```bash
   git clone https://github.com/your-org/partygames-minecraft-dart.git
   cd partygames-minecraft-dart
   ```
2. **Open in IDE**  
   IntelliJ IDEA or Eclipse recommended.  
3. **Refresh & build**  
   ```bash
   ./gradlew --refresh-dependencies
   ./gradlew clean build
   ```
4. **Run**  
   Launch your development environment or drop the generated JAR into your mods folder.

---

## Usage

1. **Place** a DartBoard block in the world.  
2. **Get** a DartItem (e.g. from `/give @p partygames:dart`).  
3. **Start** the game:  
   - Solo:  
     ```
     /dart challenge <rounds>
     ```  
   - PvP:  
     ```
     /dart challenge <rounds> <otherPlayer>
     ```  
   This resets both scores to 0 and displays “Dart Score” in the sidebar.  
4. **Throw** darts by charging the DartItem (hold right-click) and releasing at the board.  
5. **Score** updates automatically after each hit.  
6. **Stop** anytime:  
   ```
   /dart stop
   ```  
   This removes the scoreboard and ends the game.

---

## Developer Notes

- **Mappings**: Uses the official Mojang mappings. See [Mojang.md](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md)  
- **NeoForge Docs**: https://docs.neoforged.net/  
- **Discord**: https://discord.neoforged.net/  

---

## Credits

- **Joey0980 (Joey)** – Setup environment; implemented DartItem and ThrownDartEntity  
- **grcodeman (Cody)** – Created DartBoardBlock; wired scoring & commands  
- **calvinb552 (Calvin)** – Designed and modeled block/dart assets & textures  
