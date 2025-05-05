# OfflineAuth 1.7.10 Fork (Forge)
Beta Minecraft authentication system contained in a Forge mod. (Please note the beta) Please report any bugs, especially security holes.

![image](https://user-images.githubusercontent.com/19153947/178967503-51b17062-e549-4869-82c9-3e0615dc759f.png)

### Wiki
[OfflineAuth wiki](https://github.com/kotmatross28729/OfflineAuth/wiki)


### Motivation
Original: "Microsoft forcing people to migrate from Mojang to Microsoft. Oh, and now chat reports."

My: "Authentication method independent of any external servers (Mojang/Microsoft/Other third party services). A.K.A. My server - My authentication"

### Tell me more
This mod runs a small rest server (spark) alongside the main Minecraft process. This keeps everything contained on the server which could even run on a local network, independent of any external auth servers.

### Getting started
If you are a server operator:
* Drop the mod jar into mods folder. Let it generate a config.
* Your hosting solution needs to provide you with the option to open additional ports. Pick a port and set it in the config (default: `4567`). Minecraft and the rest server can not both listen on the same port.
* Tell your players about the port they need to input in order to be able to register accounts.
* Use the `/fingerprint` command to get a fingerprint of the server public key. Tell players about it too.

If you are a player:
* Drop the mod jar into your `mods` folder.
* Add server in the multiplayer menu. Go to the `Manage auth` menu.
* Change the auth port if needed, and register an account. Afterwards you should be able to join the server. If you are registering an account for the first time, you will have to confirm the public key fingerprint, and retry.

### Features
* Registration and deletion of accounts, changing account password and displayname
* Working skins implementation (with the ability to change them without leaving the game, just re-enter on server). Also, single-player support
* Allowing registration only to those who have one time tokens (generate those with the `/gentoken` command)
* Config options can be changed via file or server console
* End-to-end encryption of credentials
* Possibility of logging in with a keypair, rather than with a password
* Backport of player faces in the multiplayer tab menu (But I recommend using [TabFaces](https://github.com/JackOfNoneTrades/TabFaces))
* Integration with [Custom Main Menu](https://www.curseforge.com/minecraft/mc-mods/custom-main-menu), if a button or label has a name specified in config, a default server auth menu and server join action will be added to them
* Integration with [Server Utilities](https://github.com/GTNewHorizons/ServerUtilities) (skin in the GUIs)
* Integration with [TabFaces](https://github.com/JackOfNoneTrades/TabFaces) (skin)

### Commands
* `/changename <player> <displayname>` - Changes the username of a player (warning: this resets the user's progress)
* `/deleteplayer <identifier> (alias: delplayer)` - Delete a player account
* `/listusers (alias: luser)` - List registered users
* `/playerexists <identifier/displayname>` - Display `username` and `displayname` of a user, if registered
* `/gentoken` - Generates a one time use account creation token
* `/fingerprint` - Displays the fingerprint of the public key of the server (if users know it in advance, this can tell them if someone is performing a man in the middle attack on them)
* `/deleteskin <identifier> (alias: delskin)` - Deletes the skin of a user
* **[DEPRECATED: use [CPM](https://modrinth.com/plugin/custom-player-models) for capes]** `/deletecape  <identifier> (alias: delcape)` - Deletes the cape of a user 
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
* Matrix `#boysgregified:matrix.thisisjoes.site`.
* Website: [mcdrama.net](https://www.mcdrama.net/articles/mods.html).

### License
`LGPLv3+SNEED`

### Feel free to help translate the mod, open PR's or send lang files directly to me

[//]: # (### Coming soon &#40;with Et Futurum update&#41;:)

[//]: # (![elytra_capes]&#40;https://user-images.githubusercontent.com/19153947/181366604-53025903-521f-498c-a2ee-f61b596e15f4.gif&#41;)


## Buy me a coffee
* [ko-fi.com/jackisasubtlejoke](https://ko-fi.com/jackisasubtlejoke)
* Monero: `86aNAyad1scfNpiZ9DM7rMPsM3gbQHBhqPefbgfPunyBiGAaaTH3SvWB66HRjRMNJj9Yu7tHizYej3E7V7BEVkmNMWpWC5f`