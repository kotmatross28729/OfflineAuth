# OfflineAuth 1.7.10 (Forge)
Early alpha Minecraft authentification system contained in a Forge mod. (Please note the EARLY ALPHA) Please report any bugs, especially security holes.
It is advised to delete the `offlineauth` folder each time you update for now.

![image](https://user-images.githubusercontent.com/19153947/178967503-51b17062-e549-4869-82c9-3e0615dc759f.png)

### Motivation
Microsoft forcing people to migrate from Mojang to Microsoft and they are cringe overall.

### Tell me more
This mod runs a small rest server (spark) alongside the main Minecraft process. This keeps everything contained to the server which could even run on a local network, independent of any external auth servers, such as `vanilla` or `ely.by`.

### Getting started
If you are a server oeprator:
* Drop mod jar into mods folder. Let it generate a config.
* Your hosting solution needs to provide you with the option to open additional ports. Pick a port and set it in the config (default: 4567). Minecraft and the rest server can not both listen on the same port.
* Tell your players about the port they need to input in order to be able to register accounts.

If you are a player:
* Drop mod jar into mods folder.
* Add server in multiplayer menu. Go to the "Manage auth" menu.
* Change the port if needed, and register an account. Afterwards you should be able to join the server.

### Features
* Registration and deletion of accounts, changing account password and displayname
* Allowing registration only to those who have one time tokens
* Config options to disallow registration
* End-to-end encryption of credentials
* Possibility of logging in with a keypair, rather than with a password
* Changing configs via commands

### Coming Features
* Uploading and using skins
* Raid mitigation

### Credits
* [TechnicianLP](https://github.com/TechnicianLP) for his [ReAuth](https://github.com/TechnicianLP/ReAuth) mod. I ripped off the config and GUI to adapt them to my mods needs.
* [SinTh0r4s](https://github.com/SinTh0r4s), [basdxz](https://github.com/basdxz), and [TheElan](https://github.com/TheElan) for their [ExampleMod 1.7.10](https://github.com/SinTh0r4s/ExampleMod1.7.10) and the included gradle buildscript.
* Greg and all the people on his IRC who sometimes get my dumb ass unstuck :D

### Building
`gradlew build` should do the trick.

### Contact
* [This project GitHub repo](https://github.com/trollogyadherent/OfflineAuth), create an issue if something is broken
* Matrix rooms `#boysgregified:matrix.thisisjoes.site` and `#minecraft-modding-general:matrix.org`

### License
LGPLv3+SNEED
