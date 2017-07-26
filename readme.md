# Zenchantments
## Description
Zenchantments is a custom enchantment plugin that adds 70+ new enchantments to the game, covering a multitude of different uses. These include target-tracing arrows, lumber axes, block-breaking lasers, and much more. These custom enchantments are obtained through the normal enchantment process and act like regular enchantments, capable of being combined and merged. The plugin requires no client-side mods or resource packs. A comprehensive configuration file enables fine-tuning of individual enchantments to tailor them to every server's gameplay. ~~The plugin also adds several new arrows, which can be crafted through special recipes. These can do things like encase a target in spiderwebs, strike lightning onto the ground, and launch grenades.~~ Elemental Arrows have been removed.

## Download
See [Releases] (https://github.com/Zedly/Zenchantments/releases) for downloads

## Compile
To compile the entire project yourself, you need multiple versions of CraftBukkit as Maven repositories. These are created automatically when you use [BuildTools] (https://www.spigotmc.org/wiki/buildtools/) to obtain CB and/or Spigot. If you are making a private build and only need support for one server version, you can easily add and remove compatibility adapters by editing the parent POM and a switch in PlayerInteractUtil.

## Compatibility
The current version of this plugin is fully compatible with CraftBukkit and Spigot versions 1.10.x, 1.11.x and 1.12. Other versions have partial support through Bukkit-only fallback code, but integration with protection and logging plugins will be limited.
