# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com),
and this project follows the [Ragnarök Versioning Convention](https://github.com/Red-Studio-Ragnarok/Commons/blob/main/Ragnar%C3%B6k%20Versioning%20Convention.md).

## [Unreleased] Fancier Block Particles Version 0.8 Changelog

### Highlights

#### Revamped configs!

The new configs are easier to read understand and use, they are also faster which makes FBP have nearly no impact on loading times.

***Old Configs will be deleted so please back them up before launching if you have made major changes to them***

#### Revamped GUI

Enjoy easier to use GUI as well as a new beautiful background, this applies to every GUI in FBP.
Also take a look into the new settings page, which allows you to turn on things like debug mode and experiments, which is a new menu for experimental features.

![New gui](https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/assets/82710983/612b4e65-ddaf-4a67-8620-d2351f9c52c3)

#### Dynamic Weather System

Now weather will follow the current weather, it will rain heavier when a thunderstorm is happening.

### Added

- Dynamic Weather System (rain & snow)
- Weather Particles Render Distance option
- Added settings menu
- Added experiments menu
- Added error handling
- Added logging

### Changed

- Revamped GUI's
- Changed GUI's background, it will be a transparent background with blur while in a world and be dirt when the main menu.
- Revamped Main config file
- Changed default maximum duration from 55 to 64
- Revamped Floating Materials config file
- Fancy Rain and Fancy Snow config options have been replaced by Fancy Weather option
- Changed behavior of Fancy Block Placement with slabs placing one on top of another should be a better experience
- Changed floating materials config format to be more user-friendly and faster (Old config will be detected and replaced by the new defaults)
- Smoke particles are now slightly more transparent
- The config button in Forge's mod options now works
- Updated `mcmod.info` to feature new description and better credits
- Blacklist GUI cursor is now darker and becomes as light as before when hovering a button
- Changed freeze effect text color from orange to aqua
- You can navigate pages with the scroll wheel

### Fixed

- Fixed crashes with some moded blocks ([#7])
- Fixed crashes with some moded blocks when HWYLA is installed ([#13])
- Fixed Memory leak with `FBP##originalEffectRenderer` ([#139])
- Fixed Fancy Block Placement speed being broken because of an accidental change in 0.7
- Fixed new config having no default value
- Fixed particles being rendered on top of the water

### Removed

- Removed Rest On Floor option
- Removed the deprecated Cartoon Mode from the menu

### Optimization

- Vectors got a redesign as a result FBP should now be faster and use less VRAM
- Hex colors are now used instead of 4 separate R,G,B and A variables which makes rendering faster
- As the result of a big code cleanup FBP as a whole should now be faster, load faster and use slightly less resources
- Optimized Rendering of Fancy Block Placement and all particles as a result, they should be faster and use slightly less GPU, RAM & VRAM
- FBP now uses its own math utilities and Jafama fast math library which should result in better performance
- Lossless textures compression resulting in 3.535 KB smaller mod size

### Internal

- Revamped GUI's they are now extremely easy to use and create, and the pages components are now independent of each others
- FBP now use Red Core
- GUI's text colors now never uses decimal colors directly and should use when possible hexadecimal colors when possible
- Added documentation for Vector2D, Vector3D, and FBPRenderer
- Switched to [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) 1.4.0
- Updated to [Gradle](https://gradle.org) 8.13
- Set a minimum Gradle Daemon JVM version requirement
- Cleanup build.gradle & gradle.properties
- Cleanup the entire code
- Vectors redesign
- Major Refactors
- Renamed most of the variables from unreadable names to readable names
- Finished removing useless `@SideOnly`
- Removed useless `isRemote` checks
- Renamed the plugin from `FMLPlugin` to `FBPPlugin`
- Switched to [CurseUpdate](https://forge.curseupdate.com/) for update checking
- Project constants are now handled automatically by [gradle-buildconfig-plugin](https://github.com/gmazzo/gradle-buildconfig-plugin)
- Switched to [Gradle](https://gradle.org) Kotlin DSL
- Switched to the new Group ID
- Switched to the new standard `gradle.properties`
- Updated [foojay-resolver](https://github.com/gradle/foojay-toolchains) to version 0.9.0

##### Credits

- [Meldexun](https://github.com/Meldexun) 
- [WildMihai](https://github.com/WildMihai) for optimizations in `FBPConfigHandler` and deprecating Rest On Floor ([#9]) 

[#7]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/7
[#9]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/pull/9
[#13]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/issues/13
[#139]: https://github.com/TominoCZ/FancyBlockParticles/issues/139

---

## Fancier Block Particles Version 0.7 Changelog - 2022-10-18

### Highlights

Particles now renders 2.5 times faster (Immense thanks to Rongmario!)

### Changed

- Default key for the blacklist menu changed from none to B

### Fixed

- Fixed GUI not saving config to file

### Removed

- (Testing) Removed some checks in FBPModelHelper because I am assuming mods aren't weird
- Removed FBPVertexUtil as it was unused

### Optimization

- Optimized main class as a result, it should be slightly faster and use slightly less RAM
- Draw calls are now batched which improve rendering performance by 2.5 times
- Thanks to an immense code cleanup the FBP as a whole should be slightly faster and use slightly less RAM

### Internal

- General code cleanup

##### Credits

- [Rongmario](https://github.com/Rongmario) for batching draw calls ([#5])

[#5]: https://github.com/Red-Studio-Ragnarok/Fancier-Block-Particles/pull/5

---

## Fancier Block Particles Version 0.6 Changelog - 2022-9-22

### Changed

- Disabled buttons now appears greyed out in the menu
- Changed Blacklist GUI bar and cursor
- Changed some Blacklist GUI from green to white
- Changed description text from green to white
- Changed confirmation GUI text to be clearer about the warning and changed the color from yellow to white & red
- Changed page order so that the page with only one slider in it is the last page

### Fixed

- Description getting behind back and next buttons
- Menu showing Fancy Block Particles instead of Fancier Block Particles
- Being able to click disabled buttons in the menu

### Removed

- Removed the sliding on text when the screen is too small

### Optimization

- Reworked all the GUI code, which is now faster, smaller, and easier to work with

### Internal

- Switched every bit of text to .lang which allows anyone to create a translation for their language

---

## Fancier Block Particles Version 0.5 Changelog - 2022-9-5 [YANKED]

### Fixed

- Critical crash because ATs were not getting applied

---

## Fancier Block Particles Version 0.4 Changelog - 2022-8-5

### Changed

- Updated `mcmod.info`

---

## Fancier Block Particles Version 0.3 Changelog - 2022-8-4 

### Optimization

- Switched to AT's instead of Method Handle which leads to increased performance and cleaner code
- Optimized Particle Digging as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Smoke as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Rain as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Snow as a result, it should be slightly faster and use slightly less RAM
- Optimized Particle Manager as a result, it should be slightly faster and use slightly less RAM
- Optimized the Event Handler as a result, it should be faster and use slightly less RAM
- Optimized the Renderer as a result, it should be faster and use less RAM and VRAM

All these optimizations result in a 9% faster mod loading time, which makes Fancier Block Particles load 25% faster than Fancy Block Particles

### Internal

- General code cleanup

---

## Fancier Block Particles Version 0.2 Changelog - 2022-8-1

### Fixed

- Fixed bug report button linking to the wrong repository
- Filled empty catch blocks
- Fixed potential NullPointerException when checking for blacklisted blocks name

### Removed

- Removed cartoon mode
- Removed smooth/fast animation lighting button

### Optimization

- Made the renderer faster
- Made the Fancy Block Placing faster
- Made the particle manager slightly faster
- Made FBP PreInit slightly faster
- Made removing blacklisted blocks slighty faster

### Internal

- Finished changing the syntax
- Refactored FBPRenderUtil (In Util) to FBPRenderer (in Renderer)
- Code Cleanup

---

## Fancier Block Particles Version 0.1 Changelog - 2022-7-31

### Added

- Logo to `mcmod.info`

### Changed

- Updated `mcmod.info` description & credits
- Updated default config
- Updated default bindings

### Fixed

- Fancy Block Placing ghost blocks when placing blocks rapidly when lagging ([#69])

### Optimization

- Made the particles use slightly less ram
- Made the particle renderer slightly faster
- Made the particle manager slightly faster
- Made the particle manager use slightly less ram
- Made the Fancy Block Placing slightly faster
- Lossless compression

### Internal

- Slowly changing the syntax
- Created ModReference.java and moved everything to it
- Rename some variables
- General code cleanup

[#69]: https://github.com/TominoCZ/FancyBlockParticles/issues/69
