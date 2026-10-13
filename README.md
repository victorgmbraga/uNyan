# uNyan

![uNyan demo showing Nyan Cat, its rainbow trail, and animated stars](img/unyan-demo.png)

An interactive **Nyan Cat** demo built in Java with the
[uImpala](https://github.com/matheuscscp/uImpala) engine. The cat follows the
mouse through a space scene with an animated rainbow, stars, and looping music.

This small project demonstrates how to combine scenes, objects, animations,
mouse and keyboard input, and audio playback in uImpala. It is not a game with
levels or objectives; its purpose is to showcase these engine features.

## Demo Features

- **1152 × 720 window:** the demo runs in windowed mode and opens directly into
  the scene, without an interactive menu screen.
- **Animated Nyan Cat:** the character uses a six-frame animation and follows
  the mouse pointer inside the window.
- **Animated rainbow:** a two-frame animation repeats from the character to the
  left edge of the window, creating the colorful trail. It does not track the
  mouse's previous positions.
- **Space background:** a blue background with 30 animated stars. Some stars
  are randomly repositioned every 15 updates, adding motion to the scene.
- **Looping music:** audio from `sfx/nyan.ogg` starts automatically with the
  scene and repeats while the demo is running.
- **Low-health feedback:** the character starts with 10 health points. When
  health drops below 5, its appearance alternates between its normal colors
  and a red tint.

Health is an internal demonstration state, with no on-screen indicator. There
are no collisions, enemies, scores, or game-over conditions: reaching zero does
not end the demo, and pressing `Z` continues to decrease the value. Close and
reopen the demo to reset its initial state.

## Controls

| Input | Action |
| --- | --- |
| Move the mouse inside the window | Positions Nyan Cat and its rainbow. |
| `Z` | Decreases health by 1 per key-down event. |
| `Esc` | Exits the demo. |
| Close the window | Exits the demo. |

**Try it:** move the mouse to watch the rainbow, then press `Z` six times to
reduce health from 10 to 4 and activate the red flashing effect.

## Running on Windows

### Requirements

- 64-bit Windows with Windows PowerShell and `curl.exe` available.
- OpenGL support and audio output.
- Internet access on the first run to download tools and dependencies.

You do not need to install Java or Maven globally or modify your `PATH`.
The `run.ps1` script sets up a portable **JDK 8** and **Maven 3.9.9** in
`target/tools`.

### From Zed

1. Open the project folder in Zed.
2. Open the command palette with **Ctrl+Shift+P**.
3. Select **`task: spawn`**.
4. Select the project's uNyan run task.

The task in [`.zed/tasks.json`](.zed/tasks.json) runs the script from the project
root and explicitly uses Windows PowerShell to avoid issues with `pwsh.exe`
aliases in `WindowsApps`. You do not need to run Zed as an administrator.

### From the Terminal

Run the following command from the project root:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\run.ps1
```

The script sets up the tools, resolves dependencies, compiles the code,
extracts native libraries, and launches the demo. It always starts the program
from the project root so it can find `img/` and `sfx/`.

To compile and prepare dependencies without opening the window:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\run.ps1 -BuildOnly
```

### Legacy Dependencies and Local Cache

The demo uses `org.unbiquitous:uImpala-jse:1.0` and LWJGL 2.9.1. The original
Google Code repository was shut down, so the script recovers the libraries
from the official archive hosted by Google and configures a local mirror
without modifying `pom.xml`.

On the first run, the three ZIP downloads total approximately **265 MiB**,
in addition to Maven dependencies. The tools, recovered repository, and Maven
cache are stored in `target/tools` and reused on subsequent runs. The archived
repository may produce checksum warnings during the first build because some
artifacts do not have published checksum files.

**Warning:** deleting `target` or running `mvn clean` also removes the portable
tools and local cache. The next run will need to download them again.

## Code Structure

The demo classes are located in `src/com/victorbraga/unyan/`:

| Class | Responsibility |
| --- | --- |
| `Main` | Starts the engine and configures mouse, keyboard, video, and audio managers. |
| `Menu` | Opens the window, makes the screen and audio output available, and immediately switches to `SpaceScene`. |
| `SpaceScene` | Assembles the scene objects: background, stars, character, and music. |
| `Background` | Draws the background and handles exiting with `Esc` or closing the window. |
| `Stars` | Animates the stars and randomly changes their positions. |
| `NyanCat` | Draws the cat and rainbow, follows the mouse, and demonstrates the low-health state. |
| `Music` | Loads and plays the music on a loop. |

Visual assets are stored in `img/`, and audio is stored in `sfx/`. The `pom.xml`
file defines the Maven build, while `run.ps1` handles setup and execution on
Windows.
