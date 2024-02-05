# Minesweeper Game

This is an implementation of the [Minesweeper](https://en.wikipedia.org/wiki/Minesweeper_(video_game)) game in Kotlin and Compose Multiplatform. 

Tech stack:

- Kotlin
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) - declarative UI
- [Decompose](https://github.com/arkivanov/Decompose) - navigation and lifecycle
- [MVIKotlin](https://github.com/arkivanov/MVIKotlin) - state management

Supported targets: Desktop (JVM) and Wasm Browser.

Running desktop app: `./gradlew :composeApp:run`

Wasm browser app: https://arkivanov.github.io/Minesweeper

Controls:

- Left mouse button - dig a cell
- Right mouse button - flag a cell
- Middle mouse button (or both left and right buttons simultaneously) - dig all unflagged adjacent cells

Known issues:

- The browser app recognizes all mouse buttons as left.
- The text fields in settings are not working in the browser app.

Screenshots:

![Desktop app](assets/desktop_app.png)
