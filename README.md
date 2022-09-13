# OfflineAuth 1.7.10 (Forge)
Beta Minecraft authentication system contained in a Forge mod. (Please note the beta) Please report any bugs, especially security holes.

![image](https://user-images.githubusercontent.com/19153947/178967503-51b17062-e549-4869-82c9-3e0615dc759f.png)

### Showcase
[youtube.com/watch?v=mB4DD_z5lvQ](https://www.youtube.com/watch?v=mB4DD_z5lvQ)

### Motivation
Microsoft forcing people to migrate from Mojang to Microsoft. Oh, and now chat reports.

### Tell me more
This mod runs a small rest server (spark) alongside the main Minecraft process. This keeps everything contained on the server which could even run on a local network, independent of any external auth servers.

### Getting started
If you are a server operator:
* Drop the mod jar into mods folder. Let it generate a config.
* Your hosting solution needs to provide you with the option to open additional ports. Pick a port and set it in the config (default: `4567`). Minecraft and the rest server can not both listen on the same port.
* Tell your players about the port they need to input in order to be able to register accounts.
* Use the `/fingerprint` command to get a fingerprint of the server public key. Tell players about it too.

If you are a player:
* Drop mod jar into mods folder.
* Add server in multiplayer menu. Go to the "Manage auth" menu.
* Change the auth port if needed, and register an account. Afterwards you should be able to join the server. If you are registering an account for the first time, you will have to confirm the public key fingerprint, and retry.

### Features
* Registration and deletion of accounts, changing account password and displayname
* Working skins and capes implementation, you can even change your skin and cape in-game. Also, single-player support
* Allowing registration only to those who have one time tokens (generate those with the `/gentoken` command)
* Config options can be changed via file or server console
* End-to-end encryption of credentials
* Possibility of logging in with a keypair, rather than with a password
* Backport of player faces in the multiplayer tab menu
* Integration with [Et Futurum Requiem](https://github.com/Roadhog360/Et-Futurum-Requiem) elytras (you will have to wait for its next release, for now Et Futurum breaks OfflineAuth's skin preview)
* Integration with [Custom Main Menu](https://www.curseforge.com/minecraft/mc-mods/custom-main-menu), if a button or label has a name specified in config, a default server auth menu and server join action will be added to them 

### Commands
* `/changename <player> <displayname>` - Changes the username of a player (warning: this resets the user's progress)
* `/deleteplayer <identifier> (alias: delplayer)` - Delete a player account
* `/listusers (alias: luser)` - List registered users
* `/playerexists <identifier>` - Check if a given identifier is already registered
* `/gentoken` - Generates a one time use account creation token
* `/fingerprint` - Displays the fingerprint of the public key of the server (if users know it in advance, this can tell them if someone is performing a man in the middle attack on them)
* `/deleteskin <identifier> (alias: delskin)` - Deletes the skin of a user
* `/deletecape <identifier> (alias: delcape)` - Deletes the cape of a user
* `/oaconfig <list|help|get|set> [config_string] [value]` - Changes given config option, only usable in the server console (more granular permissions might be added)

### Credits
* [TechnicianLP](https://github.com/TechnicianLP) for his [ReAuth](https://github.com/TechnicianLP/ReAuth) mod. I ripped off the config and GUI to adapt them to my mods needs.
* [SinTh0r4s](https://github.com/SinTh0r4s), [basdxz](https://github.com/basdxz), and [TheElan](https://github.com/TheElan) for their [ExampleMod 1.7.10](https://github.com/SinTh0r4s/ExampleMod1.7.10) and the included gradle buildscript.
* [Greg](https://github.com/GregoriusT) and all the people on his IRC who sometimes get my dumb ass unstuck :D
* The player 3D render code was taken from [bspkrsCore](https://github.com/bspkrs-mods/bspkrsCore) ([CC BY-NC-SA 3.0](https://creativecommons.org/licenses/by-nc-sa/3.0/)), by [bspkrs](https://github.com/bspkrs-mods).
* [Et Futurum Requiem](https://github.com/Roadhog360/Et-Futurum-Requiem) devs
* Translations: nocturni, kotmatross28729

### Building
`gradlew build` should do the trick.

### Contact
* [This project GitHub repo](https://github.com/trollogyadherent/OfflineAuth), create an issue if something is broken, or you have a suggestion.
* Matrix rooms `#boysgregified:matrix.thisisjoes.site` and `#minecraft-modding-general:matrix.org`.
* Website: [mcdrama.net](https://www.mcdrama.net/articles/mods.html)

### License
`LGPLv3+SNEED`

### Feel free to help translate the mod, open PR's or send lang files directly to me

### Coming soon (with Et Futurum update):
![elytra_capes](https://user-images.githubusercontent.com/19153947/181366604-53025903-521f-498c-a2ee-f61b596e15f4.gif)

## Buy me a coffee
[ko-fi.com/jackisasubtlejoke](https://ko-fi.com/jackisasubtlejoke)