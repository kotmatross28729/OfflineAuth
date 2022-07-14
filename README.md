# OfflineAuth 1.7.10 (Forge)
Early alpha Minecraft authentification system contained in a Forge mod. (Please note the EARLY ALPHA) Please report any bugs, especially security holes.

![image](https://user-images.githubusercontent.com/19153947/178861328-dab8c8a1-bfae-4945-aaf0-675e7dcdb9d1.png)

### Motivation
Microsoft forcing people to migrate from Mojang to Microsoft and, frankly, this company is just plain evil. So yeah. (Also, the author does not believe in "intellectual property")

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

### Coming Features
* Commands to change certain config options
* Uploading and using skins
* Raid mitigation

### Credits
* [TechnicianLP](https://github.com/TechnicianLP) for his [ReAuth](https://github.com/TechnicianLP/ReAuth) mod. I ripped off the config and GUI to adapt them to my mods needs.
* [SinTh0r4s](https://github.com/SinTh0r4s), [basdxz](https://github.com/basdxz), and [TheElan](https://github.com/TheElan) for their [ExampleMod 1.7.10](https://github.com/SinTh0r4s/ExampleMod1.7.10) and the included gradle buildscript.

### Building
`gradlew build` should do the trick.

### Contact
* [This project GitHub repo](https://github.com/trollogyadherent/OfflineAuth), create an issue if something is broken
* Matrix rooms `#boysgregified:matrix.thisisjoes.site` and `#minecraft-modding-general:matrix.org`

### License
LGPLv3+SNEED
