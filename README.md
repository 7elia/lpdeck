# LPDECK

This is a program I wrote so that I can use my 5 year old Launchpad Mini MK1 as a streamdeck kind of thing.

**Important for when you're running this**: if you're using the `Application` configuration type in IntelliJ, closing the process will kill it, and won't let it exit gracefully.
This means that it doesn't close the MIDI devices, but more importantly that the app won't disconnect from Voicemeeter.
Voicemeeter has a limit of 8 remotes, so if you kill the app 8 times you will have to restart Voicemeeter to use it again.
This can be avoided by using the `JAR Application` configuration type. I have mine set to run Gradle's `build` task before launch.

### Features

- Spotify integration (Requires Spicetify).
- Voicemeeter integration (I'm using Banana).
- Discord integration (Requires Vencord). 

### Installation

- Clone the repository.
- Run `./gradlew build`.
- Install [Spicetify](https://spicetify.app/docs/advanced-usage/installation).
- Install [Vencord](https://vencord.dev/download/).
- Install the `lpdeck.js` Spicetify extension (found at `lpdeck-spotify/dist`, guide on how to install extensions locally is in the credits section).
- Make sure your launchpad is plugged into the PC itself. If you're using a USB hub, make sure it is on low power mode, or it won't work consistently (manual can be found in the credits section).
- Run the .jar file in `build/libs`.

### Actions

**Spotify:**
- Toggle pause.
- Toggle repeat song.
- Toggle shuffle.
- Skip to previous song.
- Skip to next song.
---
**Voicemeeter:**
- Toggle mute for Bus 4.
- Switch between input 1 and 2 for B1.
- Toggle B1 for AUX input.
- Toggle A1 for strip 1 or 2 depending on whichever one you're using.
- Toggle A2 for both virtual inputs.
---
**Discord:**
- Disconnect from the current voice channel.
- Toggle self-deafen.
- Toggle Krisp noise supression.
- Toggle screenshare (whole screen, with audio).
- Toggle streamer mode.
- Re-apply the Vencord patch and restart Discord (if connected).

### Credits

- https://github.com/OlivierCroisier/LP4J for the library that interacts with the launchpad.
  I redid the device detection to find any connected launchpad instead of just the S model.
  I also had to clone it, change the target Java versions and disable tests to compile it.
  The .jars are in the `lib` folder if anyone wants them.
- https://github.com/mattco98/Voicemeeter-JNA-Interface for the library that interacts with Voicemeeter.
  I had to basically redo the entire maven file and move the classes into actual packages for it to build.
  Again, the .jar is available for download in the `lib` folder.
- https://www.manua.ls/novation/launchpad-mini-mk2/manual for hosting the manual that made me realize I couldn't use my launchpad in a USB hub.
  This guide is for the MK2 model, but I'm pretty sure this guide still somewhat applies to the MK1 model.
  I didn't actually end up activating the bootloader.
- https://github.com/Om-Thorat/Spicetify-extension for giving me a starting point for the Spicetify extension.
