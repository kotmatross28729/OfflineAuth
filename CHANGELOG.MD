### FORK (UNOFFICIAL) RELEASES

---

---

---

# v0.3.05

* [ServerUtilities] Disable use of "nick", as it may lead to an actual "ban" of the account (configurable - `blockServerUtilitiesDisplayNameChange`, IT'S NOT RECOMMENDED TO DISABLE!)


* Legacy Conversion is now much more optimized


* [TabFaces] Improved compatibility (avoiding Overwrite in `ClientUtil.drawHoveringTextWithFaces`, `MixinClientUtil`)


* (Potentially (need testing)) fixed an issue where the player would sometimes not render in the skin selection menu


* Added several default skins


* Skin selection menu now displays 2nd layer correctly


* Added command `/myname`. Displays all possible name variables in the chat


* Added command `/myuuid`. Displays all possible player UUID's in the chat


* Added `enableCapes` config option (client)
   - If disabled (default), removes cape selection button and also doesn't render capes on players (allows you to avoid the "curse of the permanent cape" in single player)


* [ServerUtilities] Improved compatibility: OfflineAuth now creates a `userdata.json` file, similar to the server's  `usernamecache.json`, but on the client
   - (Now, if a player has cached in this file, you will be able to see his skin in the GUI's even if the player isn't on the server)


* **Fixed a MAJOR bug due to which the display name was case-sensitive when registering**
  - When registering, existing names/input MUST be compared in lowercase
  - For example, "aaa" and "AAA" are the same display name. All Minecraft logic, all commands, etc. use lowercase names
  - Also, this is how Mojang's account registration rules work - available names are compared in lowercase


* **Fixed a bug where UUID in offlineAuth was generating from lowercase username**
  - For example, if your username is `"Test" [444cf323-978c-3e83-9288-612345bfec67]`, offlineAuth generated a UUID for `"test" [530fa97a-357f-3c19-94d3-0c5c65c18fe8]`


* Fixed vanilla Minecraft server logic that caused whitelist, ban, and op commands to still refer to Mojang servers to find accounts even when server was in offline mode
  - For example, I want to add a player `“tesT” [3246a81f-3e88-38d1-ad3d-607c386b62dd]` to the whitelist (offline), but the server accessed the Mojang API, and added a player `“Test” [d8d5a923-7b20-43d8-883b-1150148d6955]`


* Fixed server always added offline players in lower case
  - For example, I wanted to add a player `"ShfdyiS” [3f2b777f-c94a-3788-8674-71c2b1e423b2]` to the whitelist, but the server was adding the player `"shfdyis” [d16a5134-2815-3253-b486-ed9bb1108f2d]`


- A small note for list-like commands:
  + IF (add) -> specify name in strict case
    + For example, to add the player "Test" to the list (whitelist, ban, op), you need to specify strictly in this register, not "test", not "TEST", not "TeSt", but specifically "Test"
  + IF (delete) -> specify name in any case
    + For example, to remove the player "Test" from the list (whitelist, ban, op), you can specify it in any case, "test", "TEST", "TeSt", "Test", it doesn't matter


* **Now users whose IPs are banned will not be able to register new accounts (configurable - `IPBanRefuseRegistration`)**
  - Also added option `IPBanFullBlock` (disabled by default) - prevents any requests to the offlineAuth server from a blocked IP


* **Added registration cooldown (configurable - `enableRegistrationCooldown`)**
  - When a user registers, their IP is added to the `registration-cooldown.json` file, which has 2 more parameters: `lastReg` and `lastRegCooldownEnd` (the structure is the same as `banned-ips.json`).
    `lastReg` - time of the last account registration.
    `lastRegCooldownEnd` - time after which the user can create a new account.
     If the user tries to register a new account while the current time < `lastRegCooldownEnd`, the registration will be refused.
     - Cooldown time is configurable - `registrationCooldownTimeValue` and `registrationCooldownTimeType`.
       `registrationCooldownTimeValue` - a numeric value, such as 1, 10, 150, etc.
       `registrationCooldownTimeType` - a string that is a unit of time, such as "MINUTE", "HOUR", "DAY", etc.
  - There is also an option `onlyOneAccountPerIP` (disabled by default).
    With it, each IP can have only 1 registered account
     - `lastRegCooldownEnd` in this case is equal to "never"


* Added command `/registrationCooldown`. Removes IP from cooldown list / clears entire list


* Now online mode is completely disabled at startup


* Added button "Delete server public key" - deletes the server's public key. It is worth using if for some reason the server's keys (public and private) were recreated
  - This happens if the server configs have been damaged or if you try to access another server that has the same IP as the one you registered on


* **Patch from 26/05/2025:**
  * Fixed incorrect display of skins in the skin selection menu if `useLegacyConversion` was enabled
  * Fixed honeydew skin not being transparent in skin selection menu


* **Patch from 06/07/2025 by Volodin-Denis:**
  * Fixed rare crash related to ServerUtilities
  * Fixed multiple incompatibilities related to automatic farms and `secureEachEntityEvent` config
  * Potentially fixed the issue where skins were not displayed


* **Patch from 07/07/2025:**
  * [ServerUtilities] Improved uuidFastCache (UUID -> username), now clears correctly


* **Patch from 10/07/2025:**
  * Fixed player in skin selection menu not showing up with some mods (NullPointerException)
  * Added button to reset render in skin selection menu
    - Useful when using CPM to fix stuck models

---

# v0.3.04
* Improvements to LegacyConversion: now it can convert both new format to old, and old to new. Also, now LegacyConversion works not only with 64x64/64x32, but with any resolution with 1:1 / 2:1 ratios respectively. [[Wiki page](https://github.com/kotmatross28729/OfflineAuth/wiki/Guide:-New-skin-format-with-CPM)]

* Fix ArrayIndexOutOfBoundsException: -1 in SkinManagmentGUI

---

# v0.3.03
* Added `useLegacyConversion` config option, required for [new skin format with CPM](https://github.com/kotmatross28729/OfflineAuth/wiki/Guide:-New-skin-format-with-CPM)

---

# v0.3.02
* Added compatibility with [TabFaces](https://github.com/JackOfNoneTrades/TabFaces)
    * Skins in the Tab menu / next to the messages in the chat work fully (skin changes dynamically)
    * Skins in the server selection menu are cached skins that are cleared when you log in to the server, so after re-entering the server you will not see the skin in the server selection menu
        * Therefore, a client config option has been added - `clearSkinCacheOnLogin`. If `clearSkinCacheOnLogin=false`, the skin cache will not be cleared when you re-enter the server, instead, a new button will be added to the "Manage Auth" menu - "Clear skin cache", so that you can manually clear the skin cache

---

# v0.3.01
## Requires [UniMixins](https://github.com/LegacyModdingMC/UniMixins/releases) to work

* Added compatibility with [ServerUtilities](https://github.com/GTNewHorizons/ServerUtilities) (skins in GUI)
* Ported config option `showQuestionMarkIfUnknown` (from TabFaces)

---

---

---

### OFFICIAL RELEASES

---

---

---

# v0.2.96

* changeplayername command works
* command delplayer kicks the banned user from server now
* command playerexists looks for both identifier and username now, prints both if user found
* information about if debug mode is enabled should be good now
* debug config now applies to server too
* Server public key is fetched at each keystroke in the port field, so users can confirm the fingerprint before hitting "register"
* a lot of debug prints were added
* minor changes allowing integration with mods, added integration to ftb lib/util
* fixed ftb util compat when requested player isn't connected
* Fixed players without the mod clientside being able to join the server. As the packet gets lost, there is a maximum 5 seconds delay to kick a player.

---

# v0.2.88

* CMM compat fixes
* All strings should be translatable
* CMM compat alerts the player if he/she tries to join server without any credentials
* Tab key cycles through input fields
* Dialog box widths are now all dynamic

---

# v0.2.84

* Fixed some bugs and hard crashes
* Russian translation thanks to kotmatross28729
* New config to enable debug logs
* New configs to show/hide keypair related stuff and config button, in auth menu
* Custom Main Menu integration (possibility to add buttons/labels that open auth settings and allow to join a predefined server)

---

# v0.2.75

* Fixed crash occuring when a skin failed to upload
* Fixed faces not loading in the tab menu if players were far away (and the client wasn't aware of them)
* Setting/removing singleplayer skins gives now a message
* Completed some more of the French localization
* Added missing translation strings
* Fixed some buttons scaling incorrectly with non-latin languages

---

# v0.2.49

* Fixed SP skins not reloading on the fly
* Fixed crash where sometimes client does crashes upon receiving skin upload refusal

