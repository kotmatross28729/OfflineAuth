# OfflineAuth 1.7.10 (Forge)
Early alpha Minecraft authentification system contained in a Forge mod.

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