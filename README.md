# OfflineAuth 1.7.10 (Forge)
Early alpha Minecraft authentification system contained in a Forge mod.

![image](https://user-images.githubusercontent.com/19153947/159351094-b181a42b-220e-4fa0-943c-c34063652919.png)

### Motivation
Microsoft forcing people to migrate from Mojang to Microsoft and, frankly, this company is just plain evil. So yeah. (Also, the author does not believe in "intellectual property")

### Tell me more
This mod runs a small rest API server (spark) alongside the main Minecraft process. This allows easy integration with your very own solutions, and talking between the client and the server while the player has not joined the server.

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
* Registration and deletion of accounts
* Config options to disallow registration

### Coming Features
* Changing password
* Commands to change certain config options
* Registration using one-time tokens
* Uploading and using skins
* Logging in via key file
* Raid mitigation

### Credits
* [TechnicianLP](https://github.com/TechnicianLP) for his [ReAuth](https://github.com/TechnicianLP/ReAuth) mod. I ripped off the config and GUI to adapt them to my mods needs.
* [SinTh0r4s](https://github.com/SinTh0r4s), [basdxz](https://github.com/basdxz), and [TheElan](https://github.com/TheElan) for their [ExampleMod 1.7.10](https://github.com/SinTh0r4s/ExampleMod1.7.10) and the included gradle buildscript.

### License
LGPLv3+SNEED
