# Cobblemon Stats Mod

A Fabric mod for Minecraft 1.21.1 that adds extra statistics for Cobblemon trainers!  
Track your progress with new custom stats such as Pokémon caught, shinies encountered, trades completed, and more.

## ✨ Features

- **New Cobblemon Statistics**:
  - Shiny Pokémon caught
  - Pokémon caught  
  - Pokémon registered in Pokédex  
  - Battles won (PVP)  
  - Pokémon reel (fishing rod)  
  - Trades completed  
- **Scoreboard Integration**: Use Minecraft’s `/scoreboard` system to display and track these stats.  
- **Configurable**: JSON-based configuration file to toggle which events broadcast to chat.  
- **Lightweight**: No extra items or mechanics — just pure stat tracking.

## 🎮 How it Works

The mod listens to Cobblemon events (catching, battling, trading, fishing, etc.) and increments corresponding custom Minecraft stats.  
These stats can be accessed through:

- The **Statistics menu** (ESC → Statistics)  
- The **Scoreboard system** with `/scoreboard objectives`  

For example:

```mcfunction
/scoreboard objectives add shinyCaught minecraft.custom:scoremons.shiny_caught
/scoreboard objectives setdisplay sidebar shinyCaught
```

This will show all players’ shiny catches in the sidebar.

## 📋 Requirements

- **Minecraft**: 1.21.1  
- **Fabric Loader**: Latest version  
- **Fabric API**: Required  
- **Cobblemon**: 1.6.0+  

## 🔧 Installation

1. Install [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api).  
2. Download and install [Cobblemon](https://www.curseforge.com/minecraft/mc-mods/cobblemon).  
3. Download this mod and place it in your `mods` folder.  
4. Launch Minecraft!  

## ⚙️ Configuration

The mod creates a configuration file at `.minecraft/config/scoremons_config.json`:  

```json
{
  "broadcastShinyCaught": true,
  "broadcastLegendaryCaught": true,
  "broadcastShinyFossilRevived": true
}
```

- `broadcastShinyCaught`: Broadcast a message when a shiny is caught.  
- `broadcastLegendaryCaught`: Broadcast a message when a legendary is caught.  
- `broadcastShinyFossilRevived`: Broadcast a message when a shiny fossil is revived.

## 🐛 Known Issues

- None currently reported. Please open an issue if you encounter one.

## 🙏 Credits

- Built with [Fabric](https://fabricmc.net/)  
- Integrates with [Cobblemon](https://cobblemon.com/)  

---

**Track your Cobblemon journey like never before! ⚡**
