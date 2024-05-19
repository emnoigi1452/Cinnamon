# About this plugin
**Cinnamon** is a fully asynchronous item scanner made with compatibility and power in mind.

> [!NOTE]
> Cinnamon is planned to be compatible with all versions above `1.12.2`

<p align="center">
  <img src="https://th.bing.com/th/id/OIF.YQaVQsNjn06cmTN4z20F9g?rs=1&pid=ImgDetMain" alt="Cinnamon" />
</p>

**How does it work?** `Cinnamon` can perform scans on the server via its data files (world data, player data) to obtain an accurate count of items on the server
Server owners can obtain an accurate count of items stored within:
- Player's inventories/Ender Chest
- Chests/Shulker boxes, minecarts, barrels...
- Item frames, furnaces...

Currently, the following search operations are supported
- Search for a specific item
- Search for items that are locked/locked by a specific player **(Requires LockedItems)**
- Search for MMOItems/MMOItems with a specific ID.

And more operations planning to be added in later releases!

**Cinnamon** supports both basic and advanced searches, and accessible both via the command-line and through a GUI.

> [!NOTE]
> You can combine perform multiple queries at the same time (in development)

# API *(Not yet implemented)*
For developers, the API will be implemented when the plugin is finalized.

# Changelogs

> [!IMPORTANT]
> This section will only display changes made to the latest version of the source code

Update **(19/05/224)**
- Added basic player data scanning
- Implemented search queries
- Switched to lucko's helper library
- Added basic startup logic
- Added support for `LockedItems` and `MMOItems` searches.
