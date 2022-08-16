package trollogyadherent.offlineauth.gui.cmm_compat;

/* This wrapper is necessary because instantiating an @Optional.interface (forge) in a class registered on the
* minecraft/forge bus, results in an error if the optional interface is not found (its mod is not loaded).
* Using a wrapper somehow works. It just werks. */
public class IActionJoinServerWrapper {
    public static Object getActionJoinServer() {
        return new ActionJoinServer();
    }
}
